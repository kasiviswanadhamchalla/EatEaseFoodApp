package com.eatease.order.controller;

import com.eatease.common.constants.OrderStatus;
import com.eatease.order.dto.OrderResponse;
import com.eatease.order.dto.PlaceOrderRequest;
import com.eatease.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestHeader("X-User-Id") Long customerId,
            @Valid @RequestBody PlaceOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(customerId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long restaurantId) {

        boolean isRestaurant = restaurantId != null;
        Long callerId = isRestaurant ? restaurantId : userId;

        return ResponseEntity.ok(orderService.getOrder(id, callerId, isRestaurant));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> myOrders(
            @RequestHeader("X-User-Id") Long customerId) {

        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/restaurant")
    public ResponseEntity<List<OrderResponse>> restaurantOrders(
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {

        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {

        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, restaurantId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {

        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
