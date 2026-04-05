package com.eatease.order.entity;

import com.eatease.common.constants.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "delivery_partner_id")
    private Long deliveryPartnerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public OrderEntity() {
    }

    public static OrderEntityBuilder builder() {
        return new OrderEntityBuilder();
    }

    public static class OrderEntityBuilder {
        private String orderNumber;
        private Long customerId;
        private Long restaurantId;
        private OrderStatus status = OrderStatus.PENDING;
        private String deliveryAddress;
        private BigDecimal totalAmount;
        private Long deliveryPartnerId;

        public OrderEntityBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderEntityBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public OrderEntityBuilder restaurantId(Long restaurantId) { this.restaurantId = restaurantId; return this; }
        public OrderEntityBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderEntityBuilder deliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; return this; }
        public OrderEntityBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderEntityBuilder deliveryPartnerId(Long deliveryPartnerId) { this.deliveryPartnerId = deliveryPartnerId; return this; }
        public OrderEntity build() {
            OrderEntity o = new OrderEntity();
            o.setOrderNumber(orderNumber);
            o.setCustomerId(customerId);
            o.setRestaurantId(restaurantId);
            o.setStatus(status);
            o.setDeliveryAddress(deliveryAddress);
            o.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
            o.setDeliveryPartnerId(deliveryPartnerId);
            return o;
        }
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
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
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items != null ? items : new ArrayList<>(); }
}
