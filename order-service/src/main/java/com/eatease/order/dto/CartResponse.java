package com.eatease.order.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Long cartId;
    private Long customerId;
    private Long restaurantId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;

    public CartResponse() { }
    public static CartResponseBuilder builder() { return new CartResponseBuilder(); }
    public static class CartResponseBuilder {
        private Long cartId;
        private Long customerId;
        private Long restaurantId;
        private List<CartItemResponse> items;
        private BigDecimal totalAmount;
        public CartResponseBuilder cartId(Long cartId) { this.cartId = cartId; return this; }
        public CartResponseBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public CartResponseBuilder restaurantId(Long restaurantId) { this.restaurantId = restaurantId; return this; }
        public CartResponseBuilder items(List<CartItemResponse> items) { this.items = items; return this; }
        public CartResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public CartResponse build() {
            CartResponse c = new CartResponse();
            c.setCartId(cartId);
            c.setCustomerId(customerId);
            c.setRestaurantId(restaurantId);
            c.setItems(items);
            c.setTotalAmount(totalAmount);
            return c;
        }
    }
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
