package com.grimveqsum.orderreservation.order;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUserId(Long userId, Sort sort);

    List<CustomerOrder> findByStatus(OrderStatus status, Sort sort);
}