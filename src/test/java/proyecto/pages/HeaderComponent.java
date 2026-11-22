package proyecto.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import proyecto.extension.AttributeBy;

/**
 * Elementos compartidos del encabezado: menu, carrito y contador.
 */
@SuppressWarnings("null")
public class HeaderComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By closeMenuButton = By.id("react-burger-cross-btn");
    private final By badge = AttributeBy.test("shopping-cart-badge");
    private final By cartLink = AttributeBy.test("shopping-cart-link");

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
        long seconds = Long.parseLong(System.getProperty("explicit.wait.seconds", "10"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    public int getBadgeCount() {
        List<WebElement> badges = driver.findElements(badge);
        if (badges.isEmpty()) {
            return 0;
        }
        String text = badges.get(0).getText();
        return text == null || text.isBlank() ? 0 : Integer.parseInt(text.trim());
    }

    public boolean isBadgeVisible() {
        List<WebElement> badges = driver.findElements(badge);
        return !badges.isEmpty() && badges.get(0).isDisplayed();
    }

    public CartPage openCart() {
        CartPage cart = new CartPage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo abrir el carrito")
                .until(d -> {
                    if (cart.isLoadedNow()) {
                        return true;
                    }
                    clickIfClickable(cartLink);
                    return false;
                });
        return cart;
    }

    public void openMenu() {
        click(menuButton);
    }

    public void closeMenu() {
        click(closeMenuButton);
    }

    public LoginPage logout() {
        LoginPage loginPage = new LoginPage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo cerrar sesion")
                .until(d -> {
                    if (loginPage.isLoadedNow()) {
                        return true;
                    }
                    if (isDisplayedNow(logoutLink)) {
                        clickIfClickable(logoutLink);
                    } else {
                        clickIfClickable(menuButton);
                    }
                    return false;
                });
        return loginPage;
    }

    private void clickIfClickable(By locator) {
        WebElement element = driver.findElement(locator);
        if (ExpectedConditions.elementToBeClickable(element).apply(driver) == null) {
            throw new ElementNotInteractableException("Elemento no clickable: " + locator);
        }
        element.click();
    }

    private boolean isDisplayedNow(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }
}
