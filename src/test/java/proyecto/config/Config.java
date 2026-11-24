package proyecto.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lee src/test/resources/config.properties.
 * Precedencia: propiedad del sistema (-Dclave=valor) > archivo > valor por defecto.
 * Las propiedades vacias (por ejemplo, las que Surefire define sin valor) se ignoran.
 */
public final class Config {

    private static final String RESOURCE = "/config.properties";
    private static final Properties FILE = load();

    private Config() {
    }

    public static String get(String key, String defaultValue) {
        String system = System.getProperty(key);
        if (system != null && !system.isBlank()) {
            return system.trim();
        }

        String file = FILE.getProperty(key);
        if (file != null && !file.isBlank()) {
            return file.trim();
        }
        return defaultValue;
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream input = Config.class.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                System.err.println("[config] No se encontro " + RESOURCE + "; se usan valores por defecto.");
                return properties;
            }
            properties.load(input);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[config] No se pudo leer " + RESOURCE + ": " + e.getMessage());
        }
        return properties;
    }
}
