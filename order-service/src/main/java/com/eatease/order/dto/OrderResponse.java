package com.eatease.order.dto;

import com.eatease.common.constants.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long restaurantId;
    private OrderStatus status;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private Long deliveryPartnerId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemResponse> items;

    public OrderResponse() { }
    public static OrderResponseBuilder builder() { return new OrderResponseBuilder(); }
    public static class OrderResponseBuilder {
        private Long id;
        private String orderNumber;
        private Long customerId;
        private Long restaurantId;
        private OrderStatus status;
        private String deliveryAddress;
        private BigDecimal totalAmount;
        private Long deliveryPartnerId;
        private Instant createdAt;
        private Instant updatedAt;
        private List<OrderItemResponse> items;
        public OrderResponseBuilder id(Long id) { this.id = id; return this; }
        public OrderResponseBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderResponseBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public OrderResponseBuilder restaurantId(Long restaurantId) { this.restaurantId = restaurantId; return this; }
        public OrderResponseBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderResponseBuilder deliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; return this; }
        public OrderResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderResponseBuilder deliveryPartnerId(Long deliveryPartnerId) { this.deliveryPartnerId = deliveryPartnerId; return this; }
        public OrderResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public OrderResponseBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public OrderResponseBuilder items(List<OrderItemResponse> items) { this.items = items; return this; }
        public OrderResponse build() {
            OrderResponse o = new OrderResponse();
            o.setId(id);
            o.setOrderNumber(orderNumber);
            o.setCustomerId(customerId);
            o.setRestaurantId(restaurantId);
            o.setStatus(status);
            o.setDeliveryAddress(deliveryAddress);
            o.setTotalAmount(totalAmount);
            o.setDeliveryPartnerId(deliveryPartnerId);
            o.setCreatedAt(createdAt);
            o.setUpdatedAt(updatedAt);
            o.setItems(items);
            return o;
        }
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Long getDeliveryPartnerId() { return deliveryPartnerId; }
    public void setDeliveryPartnerId(Long deliveryPartnerId) { this.deliveryPartnerId = deliveryPartnerId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}
