package proyecto.pages;

import java.math.BigDecimal;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import proyecto.extension.AttributeBy;

@SuppressWarnings("null")
public class CheckoutStepTwoPage extends BasePage {
    public static final String PATH = "checkout-step-two.html";
    
    private final By shippingInfo = AttributeBy.test("shipping-info-value");
    private final By paymentInfo = AttributeBy.test("payment-info-value");
    private final By subtotal = AttributeBy.test("subtotal-label");
    private final By finishButton = AttributeBy.test("finish");
    private final By cancelButton = AttributeBy.test("cancel");
    private final By total = AttributeBy.test("total-label");
    private final By tax = AttributeBy.test("tax-label");
    private final By error = AttributeBy.test("error");

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isLoaded() {
        return isVisible(subtotal);
    }

    public boolean isLoadedNow() {
        return isVisibleNow(subtotal);
    }

    public BigDecimal getSubtotal() {
        return money(text(subtotal));
    }

    public BigDecimal getTax() {
        return money(text(tax));
    }

    public BigDecimal getTotal() {
        return money(text(total));
    }

    public String getPaymentInfo() {
        return text(paymentInfo);
    }

    public String getShippingInfo() {
        return text(shippingInfo);
    }

    public CheckoutCompletePage finish() {
        CheckoutCompletePage complete = new CheckoutCompletePage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo completar la orden")
                .until(d -> {
                    if (complete.isLoadedNow()) {
                        return true;
                    }
                    clickIfClickable(finishButton);
                    return false;
                });
        return complete;
    }

    public boolean finishAndCheckSuccess() {
        CheckoutCompletePage complete = new CheckoutCompletePage(driver);
        clickIfClickable(finishButton);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> complete.isLoadedNow() || isVisibleNow(error));
        } catch (TimeoutException ignored) {
            // error_user: no navega ni muestra error
        }
        return complete.isLoadedNow();
    }

    public InventoryPage cancel() {
        InventoryPage inventory = new InventoryPage(driver);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class, ElementNotInteractableException.class)
                .withMessage("No se pudo volver al inventario desde el resumen")
                .until(d -> {
                    if (inventory.isLoadedNow()) {
                        return true;
                    }
                    clickIfClickable(cancelButton);
                    return false;
                });
        return inventory;
    }

    private BigDecimal money(String text) {
        int dollar = text.indexOf('$');
        String value = dollar >= 0 ? text.substring(dollar + 1) : text;
        return new BigDecimal(value.trim());
    }
}
