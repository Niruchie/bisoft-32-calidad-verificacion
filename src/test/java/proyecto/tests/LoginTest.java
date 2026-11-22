package proyecto.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import proyecto.base.BaseTest;
import proyecto.data.TestUsers;
import proyecto.pages.InventoryPage;

public class LoginTest extends BaseTest {

    // [CP-009] Cierre del banner de error
    // Provoca el error, cierra el banner y verifica que ya no se muestre.
    @Test
    @DisplayName("CP-009 Cierre del banner de error")
    void errorBannerCanBeClosed() {
        loginPage.login("", TestUsers.PASSWORD);

        assertTrue(loginPage.isErrorVisible(), "El banner de error no aparecio");
        loginPage.closeError();
        assertFalse(loginPage.isErrorVisible(), "El banner de error no se cerro");
    }

    // [CP-010] Acceso directo a inventario sin sesion
    // Navega directo a /inventory.html y verifica el mensaje de acceso denegado.
    @Test
    @DisplayName("CP-010 Acceso directo a inventario sin sesion")
    void directInventoryAccessWithoutSessionIsRejected() {
        openPath(InventoryPage.PATH);

        assertEquals(
                "Epic sadface: You can only access '/inventory.html' when you are logged in.",
                loginPage.getErrorMessage(),
                "El mensaje de control de acceso no coincide");
    }
}
