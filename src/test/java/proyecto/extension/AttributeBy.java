package proyecto.extension;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * Localizador por atributo, sin escribir CSS a mano en las paginas.
 * Uso:
 *   AttributeBy.test("inventory-item")
 *   AttributeBy.testId("product-card")
 */
@SuppressWarnings("null")
public class AttributeBy extends By {

    private final String attribute;
    private final String quotedValue;

    private AttributeBy(String attribute, String value) {
        if (value == null) {
            throw new IllegalArgumentException("El valor del atributo no puede ser null");
        }
        this.attribute = attribute;
        this.quotedValue = "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    public static AttributeBy test(String value) {
        return new AttributeBy("data-test", value);
    }

    public static AttributeBy testId(String value) {
        return new AttributeBy("data-testid", value);
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        return context.findElements(By.cssSelector("[" + attribute + "=" + quotedValue + "]"));
    }

    @Override
    public String toString() {
        return "AttributeBy[" + attribute + "=" + quotedValue + "]";
    }
}
