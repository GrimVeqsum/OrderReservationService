package com.grimveqsum.orderreservation.order;

import com.grimveqsum.orderreservation.common.ConflictException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerOrderTest {

    @Test
    void markPaidShouldChangeStatusToPaid() {
        CustomerOrder order = new CustomerOrder();
        order.setUserId(1L);
        order.setStatus(OrderStatus.RESERVED);
        order.setTotalPrice(BigDecimal.valueOf(1000));

        order.markPaid();

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void markPaidShouldThrowExceptionWhenOrderIsCancelled() {
        CustomerOrder order = new CustomerOrder();
        order.setUserId(1L);
        order.setStatus(OrderStatus.CANCELLED);
        order.setTotalPrice(BigDecimal.valueOf(1000));

        assertThrows(ConflictException.class, order::markPaid);
    }

    @Test
    void cancelShouldChangeStatusToCancelled() {
        CustomerOrder order = new CustomerOrder();
        order.setUserId(1L);
        order.setStatus(OrderStatus.RESERVED);
        order.setTotalPrice(BigDecimal.valueOf(1000));

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancelShouldThrowExceptionWhenOrderIsPaid() {
        CustomerOrder order = new CustomerOrder();
        order.setUserId(1L);
        order.setStatus(OrderStatus.PAID);
        order.setTotalPrice(BigDecimal.valueOf(1000));

        assertThrows(ConflictException.class, order::cancel);
    }
}