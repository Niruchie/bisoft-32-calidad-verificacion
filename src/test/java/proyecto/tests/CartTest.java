package proyecto.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import proyecto.base.BaseTest;
import proyecto.data.TestProducts;
import proyecto.data.TestUsers;
import proyecto.pages.CartPage;
import proyecto.pages.InventoryPage;

public class CartTest extends BaseTest {
    // [CP-029] Carrito lista items, precios y cantidades
    // Agrega dos productos, abre el carrito y verifica contenido, precios y contador.
    @Test
    @DisplayName("CP-029 Carrito lista items, precios y cantidades")
    void cartListsItemsPricesAndQuantities() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.addToCart(TestProducts.BIKE_LIGHT);
        CartPage cart = inventory.header().openCart();

        assertTrue(cart.isLoaded(), "El carrito no cargo");
        assertEquals(List.of(TestProducts.BACKPACK, TestProducts.BIKE_LIGHT), cart.getItemNames(),
                "Los productos del carrito no coinciden");
        assertEquals(List.of(TestProducts.BACKPACK_PRICE, TestProducts.BIKE_LIGHT_PRICE),
                cart.getItemPrices(), "Los precios del carrito no coinciden");
        assertEquals(List.of(1, 1), cart.getItemQuantities(), "Las cantidades no son 1");
        assertEquals(2, cart.getItemCount(), "El carrito no tiene dos productos");
    }

    // [CP-030] Eliminar item desde el carrito
    // Elimina un producto y verifica que la lista y el badge se actualicen.
    @Test
    @DisplayName("CP-030 Eliminar item desde el carrito")
    void removeItemFromCart() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.addToCart(TestProducts.BIKE_LIGHT);

        CartPage cart = inventory.header().openCart();
        cart.removeItem(TestProducts.BACKPACK);

        assertEquals(List.of(TestProducts.BIKE_LIGHT), cart.getItemNames(),
                "El producto eliminado sigue en el carrito");
        assertEquals(1, cart.getItemCount(), "El carrito no quedo con un producto");

        InventoryPage back = cart.continueShopping();
        assertTrue(back.isLoaded(), "No se regreso al inventario");
        assertEquals(1, back.getCartCount(), "El badge no quedo en 1");
    }

    // [CP-031] Continue Shopping regresa al inventario
    // Vuelve al inventario y verifica que el carrito se conserve.
    @Test
    @DisplayName("CP-031 Continue Shopping regresa al inventario")
    void continueShoppingReturnsToInventory() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.addToCart(TestProducts.BIKE_LIGHT);
        CartPage cart = inventory.header().openCart();
        InventoryPage back = cart.continueShopping();

        assertTrue(back.isLoaded(), "No se regreso al inventario");
        assertEquals(2, back.getCartCount(), "El carrito no se conservo");
    }

    // [CP-032] Checkout con carrito vacio debe bloquearse
    // Abre un carrito vacio, intenta checkout y verifica que no avance.
    @Test
    @Tag("defecto")
    @DisplayName("CP-032 Checkout con carrito vacio debe bloquearse")
    void checkoutWithEmptyCartIsBlocked() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        CartPage cart = inventory.header().openCart();

        assertEquals(0, cart.getItemCount(), "El carrito deberia estar vacio");
        cart.clickCheckout();

        assertTrue(cart.isLoadedNow(), "El checkout con carrito vacio no se bloqueo");
    }

    // [CP-033] Carrito se vacia tras logout/login
    // Verifica que el carrito quede vacio; SauceDemo lo conserva y el test falla como defecto.
    @Test
    @Tag("defecto")
    @DisplayName("CP-033 Carrito se vacia tras logout/login")
    void cartIsEmptyAfterLogoutAndLogin() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.header().logout();

        loginPage.login(TestUsers.STANDARD, TestUsers.PASSWORD);

        InventoryPage afterLogin = new InventoryPage(driver);
        assertTrue(afterLogin.isLoaded(), "El inventario no cargo tras el nuevo login");
        assertEquals(0, afterLogin.getCartCount(), "El carrito no quedo vacio");
        assertFalse(afterLogin.isBadgeVisible(), "El badge sigue visible tras logout/login");
    }
}
