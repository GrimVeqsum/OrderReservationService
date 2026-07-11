package com.grimveqsum.orderreservation.order;

import com.grimveqsum.orderreservation.common.ConflictException;
import com.grimveqsum.orderreservation.order.dto.CreateOrderItemRequest;
import com.grimveqsum.orderreservation.order.dto.CreateOrderRequest;
import com.grimveqsum.orderreservation.order.dto.OrderResponse;
import com.grimveqsum.orderreservation.product.Product;
import com.grimveqsum.orderreservation.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createShouldReserveProductAndSaveOrder() {
        Product product = createProduct(1L, "Keyboard", BigDecimal.valueOf(7990), 10);

        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderItemRequest(1L, 2))
        );

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.create(request);

        assertEquals(1L, response.userId());
        assertEquals(OrderStatus.RESERVED, response.status());
        assertEquals(BigDecimal.valueOf(15980), response.totalPrice());
        assertEquals(8, product.getAvailableQuantity());
        assertEquals(1, response.items().size());
        assertEquals("Keyboard", response.items().get(0).productName());
        assertEquals(2, response.items().get(0).quantity());

        verify(orderRepository).save(any(CustomerOrder.class));
    }

    @Test
    void createShouldThrowConflictExceptionWhenNotEnoughProductQuantity() {
        Product product = createProduct(1L, "Keyboard", BigDecimal.valueOf(7990), 1);

        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderItemRequest(1L, 2))
        );

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        assertThrows(ConflictException.class, () -> orderService.create(request));

        assertEquals(1, product.getAvailableQuantity());
        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void payShouldChangeOrderStatusToPaid() {
        CustomerOrder order = new CustomerOrder();
        order.setUserId(1L);
        order.setStatus(OrderStatus.RESERVED);
        order.setTotalPrice(BigDecimal.valueOf(1000));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.pay(1L);

        assertEquals(OrderStatus.PAID, response.status());
    }

    @Test
    void cancelShouldReturnProductQuantityAndCancelOrder() {
        Product product = createProduct(1L, "Keyboard", BigDecimal.valueOf(7990), 8);

        CustomerOrder order = new CustomerOrder();
        order.setUserId(1L);
        order.setStatus(OrderStatus.RESERVED);
        order.setTotalPrice(BigDecimal.valueOf(15980));

        OrderItem item = new OrderItem(
                order,
                1L,
                "Keyboard",
                BigDecimal.valueOf(7990),
                2
        );

        order.addItem(item);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        OrderResponse response = orderService.cancel(1L);

        assertEquals(OrderStatus.CANCELLED, response.status());
        assertEquals(10, product.getAvailableQuantity());
    }

    @Test
    void createShouldThrowExceptionWhenProductIsDuplicatedInOneOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(
                        new CreateOrderItemRequest(1L, 1),
                        new CreateOrderItemRequest(1L, 2)
                )
        );

        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));

        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }

    private Product createProduct(Long id, String name, BigDecimal price, Integer availableQuantity) {
        Product product = new Product();

        ReflectionTestUtils.setField(product, "id", id);
        product.setName(name);
        product.setDescription("Test product");
        product.setPrice(price);
        product.setAvailableQuantity(availableQuantity);

        return product;
    }
}