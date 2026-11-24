package proyecto.report;

import java.util.Optional;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;
import proyecto.base.BaseTest;

/**
 * Conecta JUnit 5 con ReportManager: registra estado, duracion y captura.
 * La captura se toma antes de que @AfterEach cierre el driver.
 */
public class TestReportExtension implements
        BeforeTestExecutionCallback,
        TestWatcher,
        TestExecutionExceptionHandler {

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        ReportManager.start(context);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        ReportManager.pass(context);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ReportManager.fail(context, cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        String detail = cause == null || cause.getMessage() == null ? "sin detalle" : cause.getMessage();
        ReportManager.skip(context, "Abortado: " + detail);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        ReportManager.skip(context, "Deshabilitado" + reason.map(r -> ": " + r).orElse(""));
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Object instance = context.getTestInstance().orElse(null);
        if (instance instanceof BaseTest test) {
            byte[] png = test.captureScreenshot();
            if (png != null) {
                ReportManager.attachScreenshot(context, png);
            }
        }
        throw throwable;
    }
}
