package proyecto.data;

import java.math.BigDecimal;

/**
 * Datos estables del catalogo de Swag Labs.
 * Viven en la capa de prueba, no en los Page Objects.
 */
public final class TestProducts {

    public static final int COUNT = 6;
    public static final int EMPTY_CART = 0;
    public static final int ONE_ITEM = 1;
    public static final int THREE_ITEMS = 3;

    public static final String BACKPACK = "Sauce Labs Backpack";
    public static final BigDecimal BACKPACK_PRICE = new BigDecimal("29.99");

    public static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    public static final BigDecimal BIKE_LIGHT_PRICE = new BigDecimal("9.99");

    public static final String BOLT_SHIRT = "Sauce Labs Bolt T-Shirt";
    public static final BigDecimal BOLT_SHIRT_PRICE = new BigDecimal("15.99");

    public static final String FLEECE_JACKET = "Sauce Labs Fleece Jacket";
    public static final BigDecimal FLEECE_JACKET_PRICE = new BigDecimal("49.99");

    public static final String ONESIE = "Sauce Labs Onesie";
    public static final BigDecimal ONESIE_PRICE = new BigDecimal("7.99");

    public static final String RED_SHIRT = "Test.allTheThings() T-Shirt (Red)";
    public static final BigDecimal RED_SHIRT_PRICE = new BigDecimal("15.99");

    private TestProducts() {
    }
}
