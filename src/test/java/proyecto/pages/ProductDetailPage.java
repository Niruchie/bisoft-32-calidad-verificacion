package proyecto.pages;

import java.math.BigDecimal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import proyecto.extension.AttributeBy;

public class ProductDetailPage extends BasePage {

    private final By name = AttributeBy.test("inventory-item-name");
    private final By description = AttributeBy.test("inventory-item-desc");
    private final By price = AttributeBy.test("inventory-item-price");
    private final By backButton = AttributeBy.test("back-to-products");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isLoaded() {
        return isVisible(name);
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
        click(backButton);
        return new InventoryPage(driver);
    }
}
