package proyecto.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import proyecto.extension.AttributeBy;

public class InventoryPage extends BasePage {

    private final By inventoryContainer = By.id("inventory_container");
    private final By inventoryItem = AttributeBy.test("inventory-item");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(inventoryContainer) && isVisible(inventoryItem);
    }
}
