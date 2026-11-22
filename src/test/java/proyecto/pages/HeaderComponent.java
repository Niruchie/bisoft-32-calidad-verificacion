package proyecto.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import proyecto.extension.AttributeBy;

/**
 * Elementos compartidos del encabezado: menu, carrito y contador.
 */
@SuppressWarnings("null")
public class HeaderComponent {

    private final WebDriver driver;
    private final By badge = AttributeBy.test("shopping-cart-badge");

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
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
}
