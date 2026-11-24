package proyecto.report;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Guarda la captura del fallo en <runfolder>/screenshots y devuelve la ruta.
 */
public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    static Path save(byte[] png, Path folder, String caseId) throws Exception {
        Files.createDirectories(folder);
        String safe = caseId.replaceAll("[^A-Za-z0-9._-]", "_");
        Path file = Files.createTempFile(folder, safe + "_", ".png");
        Files.write(file, png);
        return file.toAbsolutePath();
    }
}
