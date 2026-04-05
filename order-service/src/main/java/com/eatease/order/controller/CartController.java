package com.eatease.order.controller;

import com.eatease.order.dto.CartItemRequest;
import com.eatease.order.dto.CartResponse;
import com.eatease.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final OrderService orderService;

    public CartController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") Long customerId) {
        return ResponseEntity.ok(orderService.getCart(customerId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestHeader("X-User-Id") Long customerId,
                                                 @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(orderService.addToCart(customerId, request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(@RequestHeader("X-User-Id") Long customerId,
                                                   @PathVariable Long cartItemId,
                                                   @RequestParam int quantity) {
        return ResponseEntity.ok(orderService.updateCartItem(customerId, cartItemId, quantity));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long customerId) {
        orderService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
