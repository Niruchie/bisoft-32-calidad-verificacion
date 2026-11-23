package proyecto.fixtures;

import java.math.BigDecimal;
import java.math.RoundingMode;
import proyecto.data.CheckoutData;
import proyecto.data.TestProducts;
import proyecto.pages.CheckoutStepOnePage;
import proyecto.pages.CheckoutStepTwoPage;
import proyecto.pages.InventoryPage;

/**
 * Escenarios y oraculos reutilizables para la suite de Checkout.
 * No conoce localizadores; solo compone acciones de Page Objects.
 */
public final class CheckoutFixture {

    private CheckoutFixture() {
    }

    public static InventoryPage withProducts(InventoryPage inventory, String... products) {
        for (String product : products) {
            inventory.addToCart(product);
        }
        return inventory;
    }

    public static CheckoutStepOnePage openCheckout(InventoryPage inventory) {
        return inventory.header().openCart().checkout();
    }

    public static CheckoutStepTwoPage overviewWithTwoProducts(InventoryPage inventory) {
        withProducts(inventory, TestProducts.BACKPACK, TestProducts.BIKE_LIGHT);
        return openCheckout(inventory)
                .fill(CheckoutData.FIRST_NAME, CheckoutData.LAST_NAME, CheckoutData.POSTAL_CODE)
                .continueToOverview();
    }

    public static BigDecimal expectedTax(BigDecimal subtotal) {
        return subtotal.multiply(CheckoutData.TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
