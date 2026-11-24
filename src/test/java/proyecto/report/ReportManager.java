package proyecto.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.extension.ExtensionContext;
import proyecto.config.Config;

/**
 * Genera un reporte HTML por corrida en <runfolder>/reports/<execution|defect>.html.
 * La carpeta se toma de -Drunfolder o -Drun.folder; si no, target/runs/<timestamp>.
 * El tipo de reporte se toma de -Dreport.kind (el perfil defectos usa "defect").
 */
public final class ReportManager {

    private static final Pattern CASE_ID = Pattern.compile("CP-\\d{3}");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final DateTimeFormatter HUMAN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, ExtentTest> TESTS = new ConcurrentHashMap<>();
    private static final Map<String, Long> STARTS = new ConcurrentHashMap<>();
    private static final Map<String, String> SCREENSHOTS = new ConcurrentHashMap<>();
    private static final Set<String> CASE_IDS = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger TOTAL = new AtomicInteger();
    private static final AtomicInteger PASSED = new AtomicInteger();
    private static final AtomicInteger FAILED = new AtomicInteger();
    private static final AtomicInteger SKIPPED = new AtomicInteger();

    private static ExtentReports extent;
    private static ExtentSparkReporter spark;
    private static Path runFolder;
    private static Path reportFile;
    private static String reportTitle;
    private static boolean disabled;
    private static boolean hookInstalled;

    private ReportManager() {
    }

    static synchronized void start(ExtensionContext context) {
        ensureInitialized();
        if (disabled) {
            return;
        }
        STARTS.putIfAbsent(context.getUniqueId(), System.nanoTime());
        testFor(context);
    }

    static synchronized void pass(ExtensionContext context) {
        ensureInitialized();
        if (disabled) {
            return;
        }
        ExtentTest test = testFor(context);
        test.info("Duración: " + elapsed(context) + " ms");
        test.pass("Aprobado");
        PASSED.incrementAndGet();
        TOTAL.incrementAndGet();
    }

    static synchronized void fail(ExtensionContext context, Throwable cause) {
        ensureInitialized();
        if (disabled) {
            return;
        }
        ExtentTest test = testFor(context);
        test.info("Duración: " + elapsed(context) + " ms");
        test.fail(cause);
        String base64 = SCREENSHOTS.get(context.getUniqueId());
        if (base64 != null) {
            test.addScreenCaptureFromBase64String(base64, "Captura del fallo");
        }
        FAILED.incrementAndGet();
        TOTAL.incrementAndGet();
    }

    static synchronized void skip(ExtensionContext context, String reason) {
        ensureInitialized();
        if (disabled) {
            return;
        }
        ExtentTest test = testFor(context);
        test.skip(reason);
        SKIPPED.incrementAndGet();
        TOTAL.incrementAndGet();
    }

    static synchronized void attachScreenshot(ExtensionContext context, byte[] png) {
        ensureInitialized();
        if (disabled || png == null) {
            return;
        }
        try {
            Path file = ScreenshotUtil.save(png, runFolder.resolve("screenshots"), caseId(context));
            String relative = runFolder.relativize(file).toString().replace('\\', '/');
            SCREENSHOTS.put(context.getUniqueId(), Base64.getEncoder().encodeToString(png));
            ExtentTest test = TESTS.get(context.getUniqueId());
            if (test != null) {
                test.info("Captura: " + relative);
            }
        } catch (Exception e) {
            System.err.println("[report] No se pudo guardar la captura: " + e.getMessage());
        }
    }

    /**
     * Escribe el HTML. Se invoca desde el shutdown hook del JVM de pruebas.
     */
    static synchronized void flush() {
        if (extent == null) {
            return;
        }
        try {
            if (spark != null) {
                String testWord = TOTAL.get() == 1 ? "1 test" : TOTAL.get() + " tests";
                String summary = reportTitle + " - " + testWord;
                if (!CASE_IDS.isEmpty()) {
                    String caseWord = CASE_IDS.size() == 1 ? "1 caso CP" : CASE_IDS.size() + " casos CP";
                    summary += " - " + caseWord;
                }
                spark.config().setReportName(summary);
            }
            sortTests();
            extent.setSystemInfo("Casos CP", String.valueOf(CASE_IDS.size()));
            extent.setSystemInfo("Aprobados", String.valueOf(PASSED.get()));
            extent.setSystemInfo("Fallidos", String.valueOf(FAILED.get()));
            extent.setSystemInfo("Omitidos", String.valueOf(SKIPPED.get()));
            extent.flush();
        } catch (Exception e) {
            System.err.println("[report] No se pudo escribir el reporte: " + e.getMessage());
        }
    }

    private static ExtentTest testFor(ExtensionContext context) {
        return TESTS.computeIfAbsent(context.getUniqueId(), id -> {
            String display = context.getDisplayName();
            String caseId = caseId(context);
            String method = context.getTestMethod()
                    .map(m -> m.getDeclaringClass().getSimpleName() + "#" + m.getName())
                    .orElse("?");
            String description = caseId.startsWith("CP-") ? caseId + " - " + method : method;
            ExtentTest test = extent.createTest(display, description);
            if (caseId.startsWith("CP-")) {
                CASE_IDS.add(caseId);
            }
            Set<String> tags = context.getTags();
            if (!tags.isEmpty()) {
                test.assignCategory(tags.toArray(String[]::new));
            }
            test.info("Caso: " + caseId);
            test.info("Método: " + method);
            return test;
        });
    }

    private static long elapsed(ExtensionContext context) {
        Long start = STARTS.remove(context.getUniqueId());
        return start == null ? 0 : (System.nanoTime() - start) / 1_000_000;
    }

    /**
     * El HTML de Spark respeta el orden de la lista de tests del modelo.
     * Se ordena por nombre: AttributeBy..., CP-001, CP-002, ... CP-049.
     */
    private static void sortTests() {
        List<com.aventstack.extentreports.model.Test> tests = extent.getReport().getTestList();
        synchronized (tests) {
            tests.sort(Comparator.comparing(
                    test -> test.getName() == null ? "" : test.getName(),
                    String.CASE_INSENSITIVE_ORDER));
        }
    }

    private static String caseId(ExtensionContext context) {
        Matcher matcher = CASE_ID.matcher(context.getDisplayName());
        if (matcher.find()) {
            return matcher.group();
        }
        return context.getTestMethod().map(m -> m.getName()).orElse("sin-id");
    }

    private static String resolveKind() {
        String raw = Config.get("report.kind", "execution").trim().toLowerCase(Locale.ROOT);
        return raw.startsWith("defect") ? "defect" : "execution";
    }

    private static Path resolveRunFolder() {
        String explicit = firstNonBlank(
                Config.get("runfolder"),
                Config.get("run.folder"));
        if (explicit != null) {
            return Path.of(explicit).toAbsolutePath().normalize();
        }

        Path base = Path.of("target", "runs", LocalDateTime.now().format(STAMP));
        Path candidate = base;
        int suffix = 2;
        while (Files.exists(candidate)) {
            candidate = base.resolveSibling(base.getFileName() + "-" + suffix++);
        }
        return candidate.toAbsolutePath().normalize();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private static synchronized void ensureInitialized() {
        if (extent != null || disabled) {
            return;
        }
        try {
            String kind = resolveKind();
            runFolder = resolveRunFolder();
            Path reports = runFolder.resolve("reports");
            Files.createDirectories(reports);
            Files.createDirectories(runFolder.resolve("screenshots"));

            reportFile = reports.resolve(kind + ".html");
            reportTitle = kind.equals("defect")
                    ? "BISOFT-32 - Defectos conocidos"
                    : "BISOFT-32 - Ejecución estable";

            spark = new ExtentSparkReporter(reportFile.toString());
            spark.config().setDocumentTitle(reportTitle);
            spark.config().setReportName(reportTitle);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setOfflineMode(true);
            spark.config().thumbnailForBase64(true);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Suite", kind);
            extent.setSystemInfo("Run folder", runFolder.toString());
            extent.setSystemInfo("Reporte", reportFile.toString());
            extent.setSystemInfo("Navegador", Config.get("browser", "chrome"));
            extent.setSystemInfo("Headless", String.valueOf(Config.getBoolean("headless", false)));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("SO", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            extent.setSystemInfo("Inicio", LocalDateTime.now().format(HUMAN));
            if (kind.equals("defect")) {
                extent.setSystemInfo("Nota", "Los fallos de este reporte son evidencia esperada del perfil de defectos.");
            }

            if (!hookInstalled) {
                Runtime.getRuntime().addShutdownHook(new Thread(ReportManager::flush, "extent-report-flush"));
                hookInstalled = true;
            }
        } catch (Exception e) {
            disabled = true;
            System.err.println("[report] Reporte deshabilitado: " + e.getMessage());
        }
    }
}
