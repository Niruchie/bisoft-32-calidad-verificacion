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
        return new InventoryPage(driver);
    }
}
