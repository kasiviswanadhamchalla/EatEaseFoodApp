package com.eatease.order.repository;

import com.eatease.common.constants.OrderStatus;
import com.eatease.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    List<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<OrderEntity> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    List<OrderEntity> findByStatus(OrderStatus status);
}
