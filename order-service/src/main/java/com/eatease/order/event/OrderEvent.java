package com.eatease.order.event;

import com.eatease.common.constants.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderEvent {
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private Long restaurantId;
    private OrderStatus status;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private List<OrderItemEvent> items;

    public OrderEvent() {
    }

    public static OrderEventBuilder builder() {
        return new OrderEventBuilder();
    }

    public static class OrderEventBuilder {
        private Long orderId;
        private String orderNumber;
        private Long customerId;
        private Long restaurantId;
        private OrderStatus status;
        private String deliveryAddress;
        private BigDecimal totalAmount;
        private Instant createdAt;
        private List<OrderItemEvent> items;

        public OrderEventBuilder orderId(Long orderId) { this.orderId = orderId; return this; }
        public OrderEventBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderEventBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public OrderEventBuilder restaurantId(Long restaurantId) { this.restaurantId = restaurantId; return this; }
        public OrderEventBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderEventBuilder deliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; return this; }
        public OrderEventBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderEventBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public OrderEventBuilder items(List<OrderItemEvent> items) { this.items = items; return this; }
        public OrderEvent build() {
            OrderEvent e = new OrderEvent();
            e.setOrderId(orderId);
            e.setOrderNumber(orderNumber);
            e.setCustomerId(customerId);
            e.setRestaurantId(restaurantId);
            e.setStatus(status);
            e.setDeliveryAddress(deliveryAddress);
            e.setTotalAmount(totalAmount);
            e.setCreatedAt(createdAt);
            e.setItems(items);
            return e;
        }
    }

    public static class OrderItemEvent {
        private Long menuItemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal subtotal;

        public OrderItemEvent() { }
        public static OrderItemEventBuilder builder() { return new OrderItemEventBuilder(); }
        public static class OrderItemEventBuilder {
            private Long menuItemId;
            private String itemName;
            private Integer quantity;
            private BigDecimal subtotal;
            public OrderItemEventBuilder menuItemId(Long menuItemId) { this.menuItemId = menuItemId; return this; }
            public OrderItemEventBuilder itemName(String itemName) { this.itemName = itemName; return this; }
            public OrderItemEventBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
            public OrderItemEventBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
            public OrderItemEvent build() {
                OrderItemEvent e = new OrderItemEvent();
                e.setMenuItemId(menuItemId);
                e.setItemName(itemName);
                e.setQuantity(quantity);
                e.setSubtotal(subtotal);
                return e;
            }
        }
        public Long getMenuItemId() { return menuItemId; }
        public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<OrderItemEvent> getItems() { return items; }
    public void setItems(List<OrderItemEvent> items) { this.items = items; }
}
