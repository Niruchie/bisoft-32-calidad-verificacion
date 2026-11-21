package proyecto.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import proyecto.extension.AttributeBy;

public class LoginPage extends BasePage {

    public static final String URL = "https://www.saucedemo.com/";

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = AttributeBy.test("error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        open(URL);
        return this;
    }

    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }

    public String getErrorMessage() {
        return text(errorMessage);
    }

    public boolean isLoaded() {
        return isVisible(loginButton);
    }
}
