package proyecto.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
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

    /**
     * Hace clic solo si el elemento esta realmente clickable.
     * Si no, lanza ElementNotInteractableException para que el FluentWait reintente.
     */
    protected void clickIfClickable(By locator) {
        clickIfClickable(driver.findElement(locator));
    }

    protected void clickIfClickable(WebElement element) {
        if (ExpectedConditions.elementToBeClickable(element).apply(driver) == null) {
            throw new ElementNotInteractableException("Elemento no clickable");
        }
        element.click();
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

    /**
     * Chequeo inmediato, sin espera. Se usa como condicion dentro de FluentWait.
     */
    protected boolean isVisibleNow(By locator) {
        return driver.findElements(locator).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected void open(String url) {
        driver.get(url);
    }
}
