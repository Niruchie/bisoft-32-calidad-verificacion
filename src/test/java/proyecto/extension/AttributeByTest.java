package proyecto.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AttributeByTest {

    @Test
    @DisplayName("AttributeBy.test usa data-test")
    void testUsesDataTestAttribute() {
        assertEquals(
                "AttributeBy[data-test='inventory-item']",
                AttributeBy.test("inventory-item").toString());
    }

    @Test
    @DisplayName("AttributeBy.testId usa data-testid")
    void testIdUsesDataTestIdAttribute() {
        assertEquals(
                "AttributeBy[data-testid='product-card']",
                AttributeBy.testId("product-card").toString());
    }
}
