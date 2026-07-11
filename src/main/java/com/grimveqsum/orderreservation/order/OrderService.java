package com.grimveqsum.orderreservation.order;

import com.grimveqsum.orderreservation.common.NotFoundException;
import com.grimveqsum.orderreservation.order.dto.CreateOrderItemRequest;
import com.grimveqsum.orderreservation.order.dto.CreateOrderRequest;
import com.grimveqsum.orderreservation.order.dto.OrderResponse;
import com.grimveqsum.orderreservation.product.Product;
import com.grimveqsum.orderreservation.product.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        validateUniqueProducts(request.items());

        CustomerOrder order = new CustomerOrder();
        order.setUserId(request.userId());
        order.setStatus(OrderStatus.RESERVED);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = findProductForUpdate(itemRequest.productId());

            product.decreaseAvailableQuantity(itemRequest.quantity());

            OrderItem item = new OrderItem(
                    order,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    itemRequest.quantity()
            );

            order.addItem(item);

            BigDecimal itemPrice = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()));

            totalPrice = totalPrice.add(itemPrice);
        }

        order.setTotalPrice(totalPrice);

        CustomerOrder savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll(Long userId, OrderStatus status) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        if (userId != null) {
            return orderRepository.findByUserId(userId, sort)
                    .stream()
                    .map(OrderResponse::from)
                    .toList();
        }

        if (status != null) {
            return orderRepository.findByStatus(status, sort)
                    .stream()
                    .map(OrderResponse::from)
                    .toList();
        }

        return orderRepository.findAll(sort)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        CustomerOrder order = findOrderById(id);

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse pay(Long id) {
        CustomerOrder order = findOrderById(id);

        order.markPaid();

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        CustomerOrder order = findOrderById(id);

        for (OrderItem item : order.getItems()) {
            Product product = findProductForUpdate(item.getProductId());
            product.increaseAvailableQuantity(item.getQuantity());
        }

        order.cancel();

        return OrderResponse.from(order);
    }

    private CustomerOrder findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
    }

    private Product findProductForUpdate(Long id) {
        return productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    private void validateUniqueProducts(List<CreateOrderItemRequest> items) {
        Set<Long> productIds = new HashSet<>();

        for (CreateOrderItemRequest item : items) {
            if (!productIds.add(item.productId())) {
                throw new IllegalArgumentException("Product cannot be duplicated in one order. Product id: " + item.productId());
            }
        }
    }
}