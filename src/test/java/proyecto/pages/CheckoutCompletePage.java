package proyecto.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class CheckoutCompletePage extends BasePage {

    public static final String PATH = "checkout-complete.html";
    public static final String THANK_YOU = "Thank you for your order!";

    private final By header = AttributeBy.test("complete-header");
    private final By backHomeButton = AttributeBy.test("back-to-products");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isLoaded() {
        return isVisible(header);
    }

    public boolean isLoadedNow() {
        return isVisibleNow(header);
    }

    public String getHeader() {
        return text(header);
    }

    public InventoryPage backHome() {
        InventoryPage inventory = new InventoryPage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo volver al inventario desde la confirmacion")
                .until(d -> {
                    if (inventory.isLoadedNow()) {
                        return true;
                    }
                    clickIfClickable(backHomeButton);
                    return false;
                });
        return inventory;
    }
}
