package com.grimveqsum.orderreservation.product;

import com.grimveqsum.orderreservation.common.ConflictException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void decreaseAvailableQuantityShouldDecreaseQuantity() {
        Product product = new Product();
        product.setName("Keyboard");
        product.setDescription("Keyboard for developers");
        product.setPrice(BigDecimal.valueOf(7990));
        product.setAvailableQuantity(10);

        product.decreaseAvailableQuantity(3);

        assertEquals(7, product.getAvailableQuantity());
    }

    @Test
    void decreaseAvailableQuantityShouldThrowExceptionWhenNotEnoughQuantity() {
        Product product = new Product();
        product.setName("Keyboard");
        product.setDescription("Keyboard for developers");
        product.setPrice(BigDecimal.valueOf(7990));
        product.setAvailableQuantity(2);

        assertThrows(ConflictException.class, () -> product.decreaseAvailableQuantity(5));
    }

    @Test
    void increaseAvailableQuantityShouldIncreaseQuantity() {
        Product product = new Product();
        product.setName("Keyboard");
        product.setDescription("Keyboard for developers");
        product.setPrice(BigDecimal.valueOf(7990));
        product.setAvailableQuantity(5);

        product.increaseAvailableQuantity(4);

        assertEquals(9, product.getAvailableQuantity());
    }
}