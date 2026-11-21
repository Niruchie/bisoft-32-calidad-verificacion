package proyecto.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base de todos los Page Objects.
 * Concentra las esperas explicitas y las acciones genericas sobre elementos.
 */
@SuppressWarnings("null")
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        long seconds = Long.parseLong(System.getProperty("explicit.wait.seconds", "10"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    /**
     * Cada pagina define que significa estar cargada.
     */
    public abstract boolean isLoaded();

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String text(By locator) {
        return visible(locator).getText();
    }

    protected boolean isVisible(By locator) {
        try {
            return visible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected void open(String url) {
        driver.get(url);
    }
}
