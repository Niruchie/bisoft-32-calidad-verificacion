package proyecto.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import proyecto.core.DriverFactory;
import proyecto.pages.InventoryPage;
import proyecto.pages.LoginPage;

/**
 * Ciclo de vida comun de todas las pruebas:
 * un navegador nuevo por cada test, login abierto y cierre al final.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.create();
        loginPage = new LoginPage(driver).open();
    }

    @AfterEach
    void tearDown() {
        DriverFactory.quit(driver);
    }

    /**
     * Precondicion generica para las suites que necesitan una sesion iniciada.
     * No conoce usuarios concretos: los datos viven en TestUsers o en el CSV.
     * El assert del resultado lo hace cada test, no este helper.
     */
    protected InventoryPage loginAs(String username, String password) {
        loginPage.login(username, password);
        InventoryPage inventory = new InventoryPage(driver);
        inventory.isLoaded();
        return inventory;
    }

    /**
     * Navega a una ruta del sitio sin pasar por la UI.
     * Se usa para verificar control de acceso (CP-010), no para probar navegacion.
     */
    protected void openPath(String path) {
        String base = LoginPage.URL.replaceAll("/$", "");
        driver.get(base + "/" + path.replaceFirst("^/", ""));
    }
}
