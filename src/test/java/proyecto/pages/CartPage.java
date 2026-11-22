package proyecto.pages;

import java.math.BigDecimal;
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
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class CartPage extends BasePage {

    public static final String PATH = "cart.html";

    private final By item = AttributeBy.test("inventory-item");
    private final By itemName = AttributeBy.test("inventory-item-name");
    private final By itemPrice = AttributeBy.test("inventory-item-price");
    private final By itemQuantity = AttributeBy.test("item-quantity");
    private final By continueShopping = AttributeBy.test("continue-shopping");
    private final By checkout = AttributeBy.test("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isLoaded() {
        return isVisible(continueShopping);
    }

    public boolean isLoadedNow() {
        return isVisibleNow(continueShopping);
    }

    public List<String> getItemNames() {
        return driver.findElements(itemName).stream()
                .map(WebElement::getText)
                .toList();
    }

    public List<BigDecimal> getItemPrices() {
        return driver.findElements(itemPrice).stream()
                .map(element -> new BigDecimal(element.getText().replace("$", "").trim()))
                .toList();
    }

    public List<Integer> getItemQuantities() {
        return driver.findElements(itemQuantity).stream()
                .map(element -> Integer.parseInt(element.getText().trim()))
                .toList();
    }

    public int getItemCount() {
        return driver.findElements(item).size();
    }

    public void removeItem(String productName) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo quitar del carrito: " + productName)
                .until(d -> {
                    boolean present = d.findElements(itemName).stream()
                            .anyMatch(element -> element.getText().equals(productName));
                    if (!present) {
                        return true;
                    }
                    itemByName(productName).findElements(By.tagName("button")).stream()
                            .filter(button -> {
                                String testId = button.getAttribute("data-test");
                                return testId != null && testId.startsWith("remove");
                            })
                            .findFirst()
                            .ifPresent(this::clickIfClickable);
                    return false;
                });
    }

    public InventoryPage continueShopping() {
        WebElement marker = driver.findElement(continueShopping);
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

    public void checkout() {
        String before = driver.getCurrentUrl();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("El checkout no avanzo desde " + before)
                .until(d -> {
                    if (!d.getCurrentUrl().equals(before)) {
                        return true;
                    }
                    clickIfClickable(checkout);
                    return !d.getCurrentUrl().equals(before);
                });
    }

    private WebElement itemByName(String productName) {
        return driver.findElements(item).stream()
                .filter(card -> card.findElement(itemName).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado en el carrito: " + productName));
    }
}
