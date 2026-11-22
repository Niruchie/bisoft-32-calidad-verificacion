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
import org.openqa.selenium.support.ui.Select;
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class InventoryPage extends BasePage {

    public static final String PATH = "inventory.html";

    public enum SortOption {
        NAME_ASC("az"),
        NAME_DESC("za"),
        PRICE_ASC("lohi"),
        PRICE_DESC("hilo");

        private final String value;

        SortOption(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    private final By inventoryContainer = By.id("inventory_container");
    private final By inventoryItem = AttributeBy.test("inventory-item");
    private final By itemName = AttributeBy.test("inventory-item-name");
    private final By itemPrice = AttributeBy.test("inventory-item-price");
    private final By sortSelect = AttributeBy.test("product-sort-container");
    private final By itemDescription = AttributeBy.test("inventory-item-desc");

    private final HeaderComponent header;

    public InventoryPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    @Override
    public boolean isLoaded() {
        return isVisible(inventoryContainer) && isVisible(inventoryItem);
    }

    public boolean isLoadedNow() {
        return isVisibleNow(inventoryContainer) && isVisibleNow(inventoryItem);
    }

    public List<String> getProductNames() {
        return driver.findElements(itemName).stream()
                .map(WebElement::getText)
                .toList();
    }

    public List<String> getProductDescriptions() {
        return driver.findElements(itemDescription).stream()
                .map(WebElement::getText)
                .toList();
    }

    public List<BigDecimal> getProductPrices() {
        return driver.findElements(itemPrice).stream()
                .map(element -> new BigDecimal(element.getText().replace("$", "").trim()))
                .toList();
    }

    public List<String> getImageSrcs() {
        return driver.findElements(inventoryItem).stream()
                .map(item -> item.findElement(By.tagName("img")).getAttribute("src"))
                .toList();
    }

    public void sortBy(SortOption option) {
        new Select(visible(sortSelect)).selectByValue(option.value());
    }

    public void addToCart(String productName) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo agregar al carrito: " + productName)
                .until(d -> {
                    if (isButtonVisibleNow(productName, "remove")) {
                        return true;
                    }
                    clickButton(productName, "add-to-cart");
                    return isButtonVisibleNow(productName, "remove");
                });
    }

    public void removeFromCart(String productName) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo quitar del carrito: " + productName)
                .until(d -> {
                    if (isButtonVisibleNow(productName, "add-to-cart")) {
                        return true;
                    }
                    clickButton(productName, "remove");
                    return isButtonVisibleNow(productName, "add-to-cart");
                });
    }

    public boolean isAddButtonVisible(String productName) {
        return isButtonVisibleNow(productName, "add-to-cart");
    }

    public boolean isRemoveButtonVisible(String productName) {
        return isButtonVisibleNow(productName, "remove");
    }

    public int getCartCount() {
        return header.getBadgeCount();
    }

    public boolean isBadgeVisible() {
        return header.isBadgeVisible();
    }

    public HeaderComponent header() {
        return header;
    }

    public ProductDetailPage openProduct(String productName) {
        WebElement marker = itemByName(productName).findElement(itemName);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se abrio el detalle de: " + productName)
                .until(d -> {
                    if (ExpectedConditions.stalenessOf(marker).apply(d)) {
                        return true;
                    }
                    clickIfClickable(marker);
                    return false;
                });
        return new ProductDetailPage(driver);
    }

    private boolean isButtonVisibleNow(String productName, String prefix) {
        try {
            return itemByName(productName).findElements(By.tagName("button")).stream()
                    .anyMatch(button -> hasTestId(button, prefix) && button.isDisplayed());
        } catch (Exception e) {
            return false;
        }
    }

    private WebElement itemByName(String productName) {
        return driver.findElements(inventoryItem).stream()
                .filter(item -> item.findElement(itemName).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + productName));
    }

    private void clickButton(String productName, String prefix) {
        WebElement button = itemByName(productName).findElements(By.tagName("button")).stream()
                .filter(element -> hasTestId(element, prefix) && element.isDisplayed())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Boton no encontrado: " + prefix));
        clickIfClickable(button);
    }

    private boolean hasTestId(WebElement button, String prefix) {
        String testId = button.getAttribute("data-test");
        return testId != null && testId.startsWith(prefix);
    }
}
