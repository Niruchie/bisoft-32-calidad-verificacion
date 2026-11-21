package proyecto.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import proyecto.base.BaseTest;
import proyecto.pages.InventoryPage;

public class LoginDataDrivenTest extends BaseTest {
    private final String SUCCESS = "SUCCESS";

    // [CP-001..007, CP-012, CP-013] Matriz de login data-driven
    // Ejecuta login con cada fila del CSV y verifica inventario o mensaje de error.
    @ParameterizedTest(name = "{0} | usuario={1}")
    @CsvFileSource(resources = "/login_cases.csv", numLinesToSkip = 1)
    @DisplayName("CP-001..007 y CP-012..013 Matriz de login (data-driven)")
    void loginMatrix(String caseId, String username, String password, String expectedOutcome, String expectedMessage) {
        String user = username == null ? "" : username;
        String pass = password == null ? "" : password;
        loginPage.login(user, pass);

        if (SUCCESS.equals(expectedOutcome)) {
            assertTrue(new InventoryPage(driver).isLoaded(),
                    caseId + ": el inventario no cargo tras un login exitoso");
        } else {
            assertEquals(expectedMessage, loginPage.getErrorMessage(),
                    caseId + ": mensaje de error inesperado");
        }
    }
}
