package proyecto.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import proyecto.base.BaseTest;
import proyecto.data.TestUsers;
import proyecto.pages.InventoryPage;

public class LoginTest extends BaseTest {

    @Test
    @DisplayName("CP-006 Credenciales invalidas muestran el mensaje de error")
    void invalidCredentialsShowErrorMessage() {
        loginPage.login("foo", "boo");

        assertEquals(
                "Epic sadface: Username and password do not match any user in this service",
                loginPage.getErrorMessage(),
                "El mensaje de error no coincide con el esperado");
    }

    @Test
    @DisplayName("CP-002 Usuario vacio muestra el mensaje de usuario requerido")
    void emptyUsernameShowsRequiredMessage() {
        loginPage.login("", TestUsers.PASSWORD);

        assertEquals(
                "Epic sadface: Username is required",
                loginPage.getErrorMessage(),
                "El mensaje de validacion no coincide con el esperado");
    }

    @Test
    @DisplayName("CP-001 Usuario valido ingresa al inventario")
    void standardUserCanLoginSuccessfully() {
        // En la suite de login la accion se ejecuta explicitamente:
        // no se usa un helper de precondicion.
        loginPage.login(TestUsers.STANDARD, TestUsers.PASSWORD);

        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(inventoryPage.isLoaded(), "El inventario no cargo tras login valido");
        assertTrue(inventoryPage.getCurrentUrl().contains("inventory"),
                "La URL no corresponde al inventario");
    }
}
