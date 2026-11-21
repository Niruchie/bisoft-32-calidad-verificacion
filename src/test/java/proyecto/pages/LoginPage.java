package proyecto.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class LoginPage extends BasePage {

    public static final String URL = "https://www.saucedemo.com/";

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = AttributeBy.test("error");
    private final By closeErrorButton = AttributeBy.test("error-button");

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

    public void closeError() {
        click(closeErrorButton);
    }

    public boolean isErrorVisible() {
        return driver
                .findElements(errorMessage)
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean isLoaded() {
        return isVisible(loginButton);
    }
}
