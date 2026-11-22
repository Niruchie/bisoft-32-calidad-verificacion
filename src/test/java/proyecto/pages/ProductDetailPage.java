package proyecto.pages;

import java.math.BigDecimal;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class ProductDetailPage extends BasePage {
    public static final String PATH = "inventory-item.html";

    private final By name = AttributeBy.test("inventory-item-name");
    private final By description = AttributeBy.test("inventory-item-desc");
    private final By price = AttributeBy.test("inventory-item-price");
    private final By backButton = AttributeBy.test("back-to-products");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isLoaded() {
        return isVisible(backButton);
    }

    public boolean isLoadedNow() {
        return isVisibleNow(backButton);
    }

    public String getName() {
        return text(name);
    }

    public String getDescription() {
        return text(description);
    }

    public BigDecimal getPrice() {
        return new BigDecimal(text(price).replace("$", "").trim());
    }

    public InventoryPage backToProducts() {
        WebElement marker = driver.findElement(backButton);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo volver al inventario")
                .until(d -> {
                    if (ExpectedConditions.stalenessOf(marker).apply(d)) {
                        return true;
                    }
                    clickIfClickable(marker);
                    return false;
                });
        return new InventoryPage(driver);
    }
}
