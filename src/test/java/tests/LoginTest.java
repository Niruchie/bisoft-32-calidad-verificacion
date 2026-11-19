package tests;

import core.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;

public class LoginTest {

    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.create();
        loginPage = new LoginPage(driver);
        loginPage.open();
    }

    @Test(description = "Credenciales invalidas muestran el mensaje de error de SauceDemo")
    public void invalidCredentialsShowErrorMessage() {
        loginPage.login("foo", "boo");
        String expected = "Epic sadface: Username and password do not match any user in this service";
        Assert.assertEquals(loginPage.getErrorMessage(), expected,
                "El mensaje de error no coincide con el esperado");
    }

    @Test(description = "Usuario vacio muestra el mensaje de usuario requerido")
    public void emptyUsernameShowsRequiredMessage() {
        loginPage.login("", "");

        String expected = "Epic sadface: Username is required";
        Assert.assertEquals(loginPage.getErrorMessage(), expected,
                "El mensaje de validacion no coincide con el esperado");
    }

    @Test(description = "Usuario valido ingresa al inventario de SauceDemo")
    public void standardUserCanLoginSuccessfully() {
        loginPage.login("standard_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(driver);
        Assert.assertTrue(inventoryPage.isLoaded(),
                "El inventario no cargo tras login valido");
        Assert.assertTrue(inventoryPage.getCurrentUrl().contains("inventory"),
                "La URL no corresponde al inventario");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
