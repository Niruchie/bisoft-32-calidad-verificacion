package proyecto.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import proyecto.base.BaseTest;
import proyecto.data.TestProducts;
import proyecto.data.TestUsers;
import proyecto.pages.InventoryPage;
import proyecto.pages.InventoryPage.SortOption;
import proyecto.pages.ProductDetailPage;

@SuppressWarnings("null")
public class InventoryTest extends BaseTest {

    // [CP-016] Inventario muestra los 6 productos
    // Verifica que el catalogo cargue con seis nombres y seis precios.
    @Test
    @DisplayName("CP-016 Inventario muestra los 6 productos")
    void inventoryShowsSixProducts() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);

        assertTrue(inventory.isLoaded(), "El inventario no cargo");
        assertEquals(TestProducts.COUNT, inventory.getProductNames().size(), "Cantidad de nombres inesperada");
        assertEquals(TestProducts.COUNT, inventory.getProductPrices().size(), "Cantidad de precios inesperada");
    }

    // [CP-017] Ordenamiento Name (A to Z)
    // Ordena por nombre ascendente y compara contra la lista ordenada.
    @Test
    @DisplayName("CP-017 Ordenamiento Name (A to Z)")
    void sortsByNameAscending() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        List<String> expected = new ArrayList<>(inventory.getProductNames());
        expected.sort(String::compareTo);
        inventory.sortBy(SortOption.NAME_ASC);

        assertEquals(expected, inventory.getProductNames(), "El orden A-Z no coincide");
    }

    // [CP-018] Ordenamiento Name (Z to A)
    // Ordena por nombre descendente y compara contra la lista inversa.
    @Test
    @DisplayName("CP-018 Ordenamiento Name (Z to A)")
    void sortsByNameDescending() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        List<String> expected = new ArrayList<>(inventory.getProductNames());
        expected.sort(Comparator.reverseOrder());
        inventory.sortBy(SortOption.NAME_DESC);

        assertEquals(expected, inventory.getProductNames(), "El orden Z-A no coincide");
    }

    // [CP-019] Ordenamiento Price (low to high)
    // Ordena por precio ascendente y compara los valores numericos.
    @Test
    @DisplayName("CP-019 Ordenamiento Price (low to high)")
    void sortsByPriceAscending() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        List<BigDecimal> expected = new ArrayList<>(inventory.getProductPrices());
        expected.sort(Comparator.naturalOrder());
        inventory.sortBy(SortOption.PRICE_ASC);

        assertEquals(expected, inventory.getProductPrices(), "El orden de precio ascendente no coincide");
    }

    // [CP-020] Ordenamiento Price (high to low)
    // Ordena por precio descendente y compara los valores numericos.
    @Test
    @DisplayName("CP-020 Ordenamiento Price (high to low)")
    void sortsByPriceDescending() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        List<BigDecimal> expected = new ArrayList<>(inventory.getProductPrices());
        expected.sort(Comparator.reverseOrder());
        inventory.sortBy(SortOption.PRICE_DESC);

        assertEquals(expected, inventory.getProductPrices(), "El orden de precio descendente no coincide");
    }

    // [CP-021] Precios empatados conservan orden estable
    // Verifica que los dos productos de 15.99 queden adyacentes y en el mismo orden relativo.
    @Test
    @DisplayName("CP-021 Precios empatados conservan orden estable")
    void tiedPricesKeepStableOrder() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.sortBy(SortOption.PRICE_ASC);
        List<String> names = inventory.getProductNames();
        int bolt = names.indexOf(TestProducts.BOLT_SHIRT);
        int red = names.indexOf(TestProducts.RED_SHIRT);

        assertTrue(bolt >= 0, "No se encontro el producto " + TestProducts.BOLT_SHIRT);
        assertTrue(red >= 0, "No se encontro el producto " + TestProducts.RED_SHIRT);
        assertEquals(bolt + 1, red,
                "Los productos de " + TestProducts.BOLT_SHIRT_PRICE
                        + " no quedaron adyacentes en orden estable");
    }

    // [CP-022] Agregar un producto actualiza badge y boton
    // Agrega un producto y verifica contador 1 y boton Remove en esa tarjeta.
    @Test
    @DisplayName("CP-022 Agregar un producto actualiza badge y boton")
    void addOneProductUpdatesBadgeAndButton() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);

        assertEquals(TestProducts.ONE_ITEM, inventory.getCartCount(), "El contador no quedo en " + TestProducts.ONE_ITEM);
        assertTrue(inventory.isRemoveButtonVisible(TestProducts.BACKPACK), "No cambio a Remove");
    }

    // [CP-023] Agregar tres productos actualiza badge a 3
    // Agrega tres productos distintos y verifica el contador.
    @Test
    @DisplayName("CP-023 Agregar tres productos actualiza badge a 3")
    void addThreeProductsUpdatesBadge() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.addToCart(TestProducts.BIKE_LIGHT);
        inventory.addToCart(TestProducts.ONESIE);

        assertEquals(TestProducts.THREE_ITEMS, inventory.getCartCount(), "El contador no quedo en " + TestProducts.THREE_ITEMS);
    }

    // [CP-024] Quitar un producto desde el inventario
    // Agrega y quita el mismo producto, verifica contador 0 y boton Add.
    @Test
    @DisplayName("CP-024 Quitar un producto desde el inventario")
    void removeOneProductRestoresAddButton() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.removeFromCart(TestProducts.BACKPACK);

        assertEquals(TestProducts.EMPTY_CART, inventory.getCartCount(), "El contador no volvio a " + TestProducts.EMPTY_CART);
        assertTrue(inventory.isAddButtonVisible(TestProducts.BACKPACK), "No volvio a Add to cart");
    }

    // [CP-025] Badge desaparece con carrito vacio
    // Quita el ultimo producto y verifica que el badge ya no se muestre.
    @Test
    @DisplayName("CP-025 Badge desaparece con carrito vacio")
    void badgeDisappearsWhenCartIsEmpty() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);
        inventory.removeFromCart(TestProducts.BACKPACK);

        assertEquals(TestProducts.EMPTY_CART, inventory.getCartCount(), "El contador no volvio a " + TestProducts.EMPTY_CART);
        assertFalse(inventory.isBadgeVisible(), "El badge sigue visible con el carrito vacio");
    }

    // [CP-026] Detalle de producto muestra sus datos
    // Abre un producto y compara nombre, precio y descripcion contra el catalogo.
    @Test
    @DisplayName("CP-026 Detalle de producto muestra sus datos")
    void productDetailShowsNamePriceAndDescription() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        ProductDetailPage detail = inventory.openProduct(TestProducts.BACKPACK);

        assertTrue(detail.isLoaded(), "El detalle no cargo");
        assertEquals(TestProducts.BACKPACK, detail.getName(), "Nombre del detalle inesperado");
        assertEquals(TestProducts.BACKPACK_PRICE, detail.getPrice(), "Precio del detalle inesperado");
        assertFalse(detail.getDescription().isBlank(), "La descripcion esta vacia");
    }

    // [CP-027] Carrito persiste al abrir detalle y volver
    // Agrega un producto, entra al detalle, vuelve y verifica que el badge siga en 1.
    @Test
    @DisplayName("CP-027 Carrito persiste al abrir detalle y volver")
    void cartPersistsAfterProductDetail() {
        InventoryPage inventory = loginAs(TestUsers.STANDARD, TestUsers.PASSWORD);
        inventory.addToCart(TestProducts.BACKPACK);

        ProductDetailPage detail = inventory.openProduct(TestProducts.BACKPACK);
        InventoryPage back = detail.backToProducts();
        assertTrue(back.isLoaded(), "No se regreso al inventario");
        assertEquals(TestProducts.ONE_ITEM, back.getCartCount(), "El carrito no se conservo al volver");
    }

    // [CP-028] problem_user: integridad del inventario
    // Verifica imagenes unicas, ordenamiento y add to cart con el usuario con defectos.
    @Test
    @Tag("defecto")
    @DisplayName("CP-028 problem_user: integridad del inventario")
    void problemUserInventoryIntegrity() {
        InventoryPage inventory = loginAs(TestUsers.PROBLEM, TestUsers.PASSWORD);
        assertTrue(inventory.isLoaded(), "El inventario de problem_user no cargo");
        List<String> images = inventory.getImageSrcs();

        assertAll(
                () -> assertEquals(TestProducts.COUNT, new HashSet<>(images).size(),
                        "Las imagenes de producto no son unicas"),
                () -> {
                    inventory.sortBy(SortOption.NAME_DESC);
                    List<String> names = inventory.getProductNames();
                    List<String> expected = new ArrayList<>(names);
                    expected.sort(Comparator.reverseOrder());
                    assertEquals(expected, names, "El ordenamiento no se aplico");
                },
                () -> {
                    inventory.addToCart(TestProducts.BACKPACK);
                    assertTrue(inventory.isRemoveButtonVisible(TestProducts.BACKPACK),
                            "No se agrego el producto correcto");
                    assertEquals(TestProducts.ONE_ITEM, inventory.getCartCount(), "El contador no quedo en " + TestProducts.ONE_ITEM);
                }
        );
    }
}
