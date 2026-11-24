package proyecto.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import proyecto.base.BaseTest;
import proyecto.data.TestProducts;
import proyecto.data.TestUsers;
import proyecto.pages.CartPage;
import proyecto.pages.HeaderComponent;
import proyecto.pages.InventoryPage;

public class MenuSessionTest extends BaseTest {

    // [CP-047] Menu abre y cierra
    // Abre el menu hamburguesa, verifica su estado y lo cierra con la X.
    @Test
    @DisplayName("CP-047 Menu abre y cierra")
    void menuOpensAndCloses() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        HeaderComponent header = inventory.header();

        header.openMenu();
        assertTrue(header.isMenuOpen(), "El menu no se abrio");

        header.closeMenu();
        assertTrue(header.isMenuClosed(), "El menu no se cerro");
    }

    // [CP-048] Reset App State limpia el carrito
    // Agrega dos productos, reinicia el estado desde el menu y verifica contador y carrito vacios.
    @Test
    @DisplayName("CP-048 Reset App State limpia el carrito")
    void resetAppStateClearsCart() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.addToCart(TestProducts.BIKE_LIGHT);
        assertEquals(TestProducts.TWO_ITEMS, inventory.getCartCount(),
                "El carrito no tenia dos productos antes del reset");

        inventory.header().resetAppState();

        assertAll(
                () -> assertEquals(TestProducts.EMPTY_CART, inventory.getCartCount(),
                        "El contador no se reinicio"),
                () -> assertFalse(inventory.isBadgeVisible(), "El badge sigue visible tras el reset"),
                () -> assertTrue(inventory.isLoaded(), "Reset App State cerro la sesion o cambio de pagina"));

        CartPage cart = inventory.header().openCart();
        assertEquals(TestProducts.EMPTY_CART, cart.getItemCount(), "El carrito no quedo vacio");

        InventoryPage back = cart.continueShopping();
        assertTrue(back.isAddButtonVisible(TestProducts.BACKPACK),
                "El producto no volvio a estar disponible para agregar");
    }

    // [CP-049] Badge cambia de numero al agregar y quitar
    // Parte de un articulo, agrega otro y quita uno; verifica 1, 2 y 1 en el header.
    @Test
    @DisplayName("CP-049 Badge cambia de numero al agregar y quitar")
    void badgeNumberChangesWhenAddingAndRemovingItems() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        HeaderComponent header = inventory.header();

        assertEquals(TestProducts.ONE_ITEM, header.getBadgeCount(),
                "El badge no comenzo en " + TestProducts.ONE_ITEM);

        inventory.addToCart(TestProducts.BIKE_LIGHT);
        assertEquals(TestProducts.TWO_ITEMS, header.getBadgeCount(),
                "El badge no subio a " + TestProducts.TWO_ITEMS);

        inventory.removeFromCart(TestProducts.BIKE_LIGHT);
        assertEquals(TestProducts.ONE_ITEM, header.getBadgeCount(),
                "El badge no bajo a " + TestProducts.ONE_ITEM);
    }
}
