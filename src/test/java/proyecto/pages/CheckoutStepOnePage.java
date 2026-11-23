package proyecto.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class CheckoutStepOnePage extends BasePage {
    public static final String POSTAL_CODE_REQUIRED = "Error: Postal Code is required";
    public static final String FIRST_NAME_REQUIRED = "Error: First Name is required";
    public static final String LAST_NAME_REQUIRED = "Error: Last Name is required";
    public static final String PATH = "checkout-step-one.html";

    private final By continueButton = AttributeBy.test("continue");
    private final By cancelButton = AttributeBy.test("cancel");
    private final By error = AttributeBy.test("error");
    private final By postalCode = By.id("postal-code");
    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isLoaded() {
        return isVisible(firstName);
    }

    public boolean isLoadedNow() {
        return isVisibleNow(firstName);
    }

    public CheckoutStepOnePage fill(String first, String last, String zip) {
        type(firstName, first);
        type(lastName, last);
        type(postalCode, zip);
        return this;
    }

    public CheckoutStepTwoPage continueToOverview() {
        CheckoutStepTwoPage overview = new CheckoutStepTwoPage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo continuar al resumen del checkout")
                .until(d -> {
                    if (overview.isLoadedNow()) {
                        return true;
                    }
                    clickIfClickable(continueButton);
                    return false;
                });
        return overview;
    }

    public String continueExpectingError() {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No aparecio el error de validacion del checkout")
                .until(d -> {
                    if (isVisibleNow(error)) {
                        return true;
                    }
                    clickIfClickable(continueButton);
                    return false;
                });
        return text(error);
    }

    public boolean continueAndCheckSuccess() {
        CheckoutStepTwoPage overview = new CheckoutStepTwoPage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("El checkout no avanzo ni mostro error")
                .until(d -> {
                    if (overview.isLoadedNow() || isVisibleNow(error)) {
                        return true;
                    }
                    clickIfClickable(continueButton);
                    return false;
                });
        return overview.isLoadedNow();
    }

    public CartPage cancel() {
        CartPage cart = new CartPage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo volver al carrito")
                .until(d -> {
                    if (cart.isLoadedNow()) {
                        return true;
                    }
                    clickIfClickable(cancelButton);
                    return false;
                });
        return cart;
    }
}
