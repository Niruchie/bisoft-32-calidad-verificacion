package proyecto.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import proyecto.base.BaseTest;
import proyecto.data.CheckoutData;
import proyecto.data.TestProducts;
import proyecto.data.TestUsers;
import proyecto.fixtures.CheckoutFixture;
import proyecto.pages.CartPage;
import proyecto.pages.CheckoutCompletePage;
import proyecto.pages.CheckoutStepOnePage;
import proyecto.pages.CheckoutStepTwoPage;
import proyecto.pages.InventoryPage;

public class CheckoutTest extends BaseTest {

    // [CP-034] Compra completa con standard_user
    // Agrega dos productos, completa el checkout y verifica totales y confirmacion.
    @Test
    @DisplayName("CP-034 Compra completa con standard_user")
    void completePurchaseWithStandardUser() {
        InventoryPage inventory = CheckoutFixture.withProducts(
                loginAs(TestUsers.STANDARD, TestUsers.PASSWORD),
                TestProducts.BACKPACK,
                TestProducts.BIKE_LIGHT);
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(inventory);

        CheckoutStepTwoPage overview = stepOne
                .fill(CheckoutData.FIRST_NAME, CheckoutData.LAST_NAME, CheckoutData.POSTAL_CODE)
                .continueToOverview();

        BigDecimal subtotal = TestProducts.BACKPACK_PRICE.add(TestProducts.BIKE_LIGHT_PRICE);
        BigDecimal tax = CheckoutFixture.expectedTax(subtotal);

        assertEquals(subtotal, overview.getSubtotal(), "Subtotal inesperado");
        assertEquals(tax, overview.getTax(), "Impuesto inesperado");
        assertEquals(subtotal.add(tax), overview.getTotal(), "Total inesperado");

        CheckoutCompletePage complete = overview.finish();
        assertTrue(complete.isLoaded(), "No se mostro la confirmacion");
        assertEquals(CheckoutCompletePage.THANK_YOU, complete.getHeader(), "Mensaje de confirmacion inesperado");

        InventoryPage back = complete.backHome();
        assertTrue(back.isLoaded(), "No se regreso al inventario");
        assertEquals(TestProducts.EMPTY_CART, back.getCartCount(), "El carrito no quedo vacio");
    }

    // [CP-035] First Name vacio
    // Intenta continuar sin First Name y verifica el mensaje exacto.
    @Test
    @DisplayName("CP-035 First Name vacio")
    void firstNameIsRequired() {
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(
                CheckoutFixture.withProducts(
                        loginAs(TestUsers.STANDARD, TestUsers.PASSWORD),
                        TestProducts.BACKPACK));

        stepOne.fill(CheckoutData.EMPTY, CheckoutData.LAST_NAME, CheckoutData.POSTAL_CODE);

        assertEquals(CheckoutStepOnePage.FIRST_NAME_REQUIRED, stepOne.continueExpectingError());
    }

    // [CP-036] Last Name vacio
    // Intenta continuar sin Last Name y verifica el mensaje exacto.
    @Test
    @DisplayName("CP-036 Last Name vacio")
    void lastNameIsRequired() {
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(
                CheckoutFixture.withProducts(
                        loginAs(TestUsers.STANDARD, TestUsers.PASSWORD),
                        TestProducts.BACKPACK));

        stepOne.fill(CheckoutData.FIRST_NAME, CheckoutData.EMPTY, CheckoutData.POSTAL_CODE);

        assertEquals(CheckoutStepOnePage.LAST_NAME_REQUIRED, stepOne.continueExpectingError());
    }

    // [CP-037] Postal Code vacio
    // Intenta continuar sin Postal Code y verifica el mensaje exacto.
    @Test
    @DisplayName("CP-037 Postal Code vacio")
    void postalCodeIsRequired() {
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(
                CheckoutFixture.withProducts(
                        loginAs(TestUsers.STANDARD, TestUsers.PASSWORD),
                        TestProducts.BACKPACK));

        stepOne.fill(CheckoutData.FIRST_NAME, CheckoutData.LAST_NAME, CheckoutData.EMPTY);

        assertEquals(CheckoutStepOnePage.POSTAL_CODE_REQUIRED, stepOne.continueExpectingError());
    }

    // [CP-038] Postal Code de 1 caracter
    // Verifica que un codigo postal de un caracter sea aceptado.
    @Test
    @DisplayName("CP-038 Postal Code de 1 caracter")
    void postalCodeWithOneCharacterIsAccepted() {
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(
                CheckoutFixture.withProducts(
                        loginAs(TestUsers.STANDARD, TestUsers.PASSWORD),
                        TestProducts.BACKPACK));

        stepOne.fill(CheckoutData.FIRST_NAME, CheckoutData.LAST_NAME, CheckoutData.POSTAL_CODE_ONE_CHAR);
        CheckoutStepTwoPage overview = stepOne.continueToOverview();

        assertTrue(overview.isLoaded(), "No se acepto el codigo postal de 1 caracter");
    }

    // [CP-039] Postal Code de 10 caracteres
    // Verifica que un codigo postal de diez caracteres sea aceptado.
    @Test
    @DisplayName("CP-039 Postal Code de 10 caracteres")
    void postalCodeWithTenCharactersIsAccepted() {
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(
                CheckoutFixture.withProducts(
                        loginAs(TestUsers.STANDARD, TestUsers.PASSWORD),
                        TestProducts.BACKPACK));

        stepOne.fill(CheckoutData.FIRST_NAME, CheckoutData.LAST_NAME, CheckoutData.POSTAL_CODE_TEN_CHARS);
        CheckoutStepTwoPage overview = stepOne.continueToOverview();

        assertTrue(overview.isLoaded(), "No se acepto el codigo postal de 10 caracteres");
    }

    // [CP-040] Cancel en paso 1 vuelve al carrito
    // Cancela el paso 1 y verifica que el carrito conserve los productos.
    @Test
    @DisplayName("CP-040 Cancel en paso 1 vuelve al carrito")
    void cancelStepOneReturnsToCart() {
        InventoryPage inventory = CheckoutFixture.withProducts(
                loginAs(TestUsers.STANDARD, TestUsers.PASSWORD),
                TestProducts.BACKPACK,
                TestProducts.BIKE_LIGHT);
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(inventory);

        CartPage back = stepOne.cancel();

        assertTrue(back.isLoaded(), "No se regreso al carrito");
        assertEquals(TestProducts.TWO_ITEMS, back.getItemCount(), "El carrito no conservo los productos");
    }

    // [CP-041] Subtotal + impuesto = total
    // Verifica la aritmetica del resumen con dos productos conocidos.
    @Test
    @DisplayName("CP-041 Subtotal + impuesto = total")
    void totalsAreCalculatedCorrectly() {
        CheckoutStepTwoPage overview = CheckoutFixture.overviewWithTwoProducts(
                loginAs(TestUsers.STANDARD, TestUsers.PASSWORD));

        BigDecimal subtotal = TestProducts.BACKPACK_PRICE.add(TestProducts.BIKE_LIGHT_PRICE);
        BigDecimal tax = CheckoutFixture.expectedTax(subtotal);

        assertEquals(subtotal, overview.getSubtotal(), "Subtotal inesperado");
        assertEquals(tax, overview.getTax(), "Impuesto inesperado");
        assertEquals(subtotal.add(tax), overview.getTotal(), "Total inesperado");
    }

    // [CP-042] Cancel en paso 2 vuelve al inventario
    // Cancela el resumen y verifica que se regrese al inventario.
    @Test
    @DisplayName("CP-042 Cancel en paso 2 vuelve al inventario")
    void cancelStepTwoReturnsToInventory() {
        CheckoutStepTwoPage overview = CheckoutFixture.overviewWithTwoProducts(
                loginAs(TestUsers.STANDARD, TestUsers.PASSWORD));

        InventoryPage back = overview.cancel();

        assertTrue(back.isLoaded(), "No se regreso al inventario desde el resumen");
    }

    // [CP-043] Finish muestra confirmacion
    // Completa la orden y verifica el mensaje de confirmacion.
    @Test
    @DisplayName("CP-043 Finish muestra confirmacion")
    void finishShowsConfirmation() {
        CheckoutStepTwoPage overview = CheckoutFixture.overviewWithTwoProducts(
                loginAs(TestUsers.STANDARD, TestUsers.PASSWORD));

        CheckoutCompletePage complete = overview.finish();

        assertTrue(complete.isLoaded(), "No se mostro la confirmacion");
        assertEquals(CheckoutCompletePage.THANK_YOU, complete.getHeader(), "Mensaje de confirmacion inesperado");
    }

    // [CP-044] Back Home vuelve al inventario
    // Desde la confirmacion vuelve al inventario y verifica el carrito vacio.
    @Test
    @DisplayName("CP-044 Back Home vuelve al inventario")
    void backHomeReturnsToInventory() {
        CheckoutCompletePage complete = CheckoutFixture.overviewWithTwoProducts(
                loginAs(TestUsers.STANDARD, TestUsers.PASSWORD)).finish();

        InventoryPage back = complete.backHome();

        assertTrue(back.isLoaded(), "No se regreso al inventario desde la confirmacion");
        assertEquals(TestProducts.EMPTY_CART, back.getCartCount(), "El carrito no quedo vacio");
    }

    // [CP-045] error_user: Continue en paso 1 avanza
    // Verifica que error_user pueda continuar; Continue funciona, el defecto esta en Finish.
    @Test
    @DisplayName("CP-045 error_user: Continue en paso 1 avanza")
    void errorUserCanContinueToOverview() {
        InventoryPage inventory = CheckoutFixture.withProducts(
                loginAs(TestUsers.ERROR, TestUsers.PASSWORD),
                TestProducts.BACKPACK);
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(inventory);

        stepOne.fill(CheckoutData.FIRST_NAME, CheckoutData.LAST_NAME, CheckoutData.POSTAL_CODE);

        assertTrue(stepOne.continueAndCheckSuccess(),
                "error_user no pudo continuar al resumen del checkout");
    }

    // [CP-046] error_user: Finish completa la orden
    // Intenta completar la orden con error_user; el defecto lo impide.
    @Test
    @Tag("defecto")
    @DisplayName("CP-046 error_user: Finish completa la orden")
    void errorUserCanFinishOrder() {
        InventoryPage inventory = CheckoutFixture.withProducts(
                loginAs(TestUsers.ERROR, TestUsers.PASSWORD),
                TestProducts.BACKPACK);
        CheckoutStepOnePage stepOne = CheckoutFixture.openCheckout(inventory);

        stepOne.fill(CheckoutData.FIRST_NAME, CheckoutData.LAST_NAME, CheckoutData.POSTAL_CODE);
        assertTrue(stepOne.continueAndCheckSuccess(),
                "error_user no llego al resumen: bloqueado en el paso 1");

        CheckoutStepTwoPage overview = new CheckoutStepTwoPage(driver);
        assertTrue(overview.finishAndCheckSuccess(),
                "error_user no pudo completar la orden");
    }
}
