package proyecto.core;

import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Crea y cierra el navegador.
 * El navegador y el modo headless se eligen por propiedades del sistema:
 *   mvn test -Dbrowser=firefox -Dheadless=true
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox(headless);
            case "edge" -> createEdge(headless);
            default -> createChrome(headless);
        };

        if (!headless) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    public static void quit(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }

    private static WebDriver createChrome(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("autofill.profile_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-features=PasswordLeakDetection,PasswordManagerOnboarding");
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1440,900");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdge(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1440,900");
        }
        return new EdgeDriver(options);
    }
}
