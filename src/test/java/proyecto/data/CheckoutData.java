package proyecto.data;

import java.math.BigDecimal;

/**
 * Datos de comprador y reglas del checkout de Swag Labs.
 * Viven en la capa de prueba, no en los Page Objects.
 */
public final class CheckoutData {

    public static final String EMPTY = "";
    public static final String FIRST_NAME = "Ana";
    public static final String LAST_NAME = "Perez";
    public static final String POSTAL_CODE = "10101";
    public static final String POSTAL_CODE_ONE_CHAR = "1";
    public static final String POSTAL_CODE_TEN_CHARS = "1234567890";
    public static final BigDecimal TAX_RATE = new BigDecimal("0.08");

    private CheckoutData() {
    }
}
