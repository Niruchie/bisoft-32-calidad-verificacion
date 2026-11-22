package proyecto.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class InventoryPage extends BasePage {

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
        actionButton(itemByName(productName), "add-to-cart").click();
    }

    public void removeFromCart(String productName) {
        actionButton(itemByName(productName), "remove").click();
    }

    public boolean isAddButtonVisible(String productName) {
        return hasButton(itemByName(productName), "add-to-cart");
    }

    public boolean isRemoveButtonVisible(String productName) {
        return hasButton(itemByName(productName), "remove");
    }

    public int getCartCount() {
        return header.getBadgeCount();
    }

    public boolean isBadgeVisible() {
        return header.isBadgeVisible();
    }

    public ProductDetailPage openProduct(String productName) {
        itemByName(productName).findElement(itemName).click();
        return new ProductDetailPage(driver);
    }

    private WebElement itemByName(String productName) {
        return driver.findElements(inventoryItem).stream()
                .filter(item -> item.findElement(itemName).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + productName));
    }

    private WebElement actionButton(WebElement item, String prefix) {
        return item.findElements(By.tagName("button")).stream()
                .filter(button -> hasTestId(button, prefix))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Boton no encontrado: " + prefix));
    }

    private boolean hasButton(WebElement item, String prefix) {
        return item.findElements(By.tagName("button")).stream()
                .anyMatch(button -> hasTestId(button, prefix) && button.isDisplayed());
    }

    private boolean hasTestId(WebElement button, String prefix) {
        String testId = button.getAttribute("data-test");
        return testId != null && testId.startsWith(prefix);
    }
}
