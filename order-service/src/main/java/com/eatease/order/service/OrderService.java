package com.eatease.order.service;

import com.eatease.common.constants.OrderStatus;
import com.eatease.order.dto.*;

import java.util.List;

public interface OrderService {
    CartResponse addToCart(Long customerId, CartItemRequest request);
    CartResponse getCart(Long customerId);
    CartResponse updateCartItem(Long customerId, Long cartItemId, int quantity);
    void clearCart(Long customerId);
    OrderResponse placeOrder(Long customerId, PlaceOrderRequest request);
    OrderResponse getOrder(Long orderId, Long callerUserId, boolean isRestaurant);
    List<OrderResponse> getOrdersByCustomer(Long customerId);
    List<OrderResponse> getOrdersByRestaurant(Long restaurantId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status, Long restaurantId);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
}
