package com.eatease.order.dto;

import java.math.BigDecimal;

public class CartItemResponse {
    private Long cartItemId;
    private Long menuItemId;
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemResponse() { }
    public static CartItemResponseBuilder builder() { return new CartItemResponseBuilder(); }
    public static class CartItemResponseBuilder {
        private Long cartItemId;
        private Long menuItemId;
        private String itemName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        public CartItemResponseBuilder cartItemId(Long cartItemId) { this.cartItemId = cartItemId; return this; }
        public CartItemResponseBuilder menuItemId(Long menuItemId) { this.menuItemId = menuItemId; return this; }
        public CartItemResponseBuilder itemName(String itemName) { this.itemName = itemName; return this; }
        public CartItemResponseBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public CartItemResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public CartItemResponseBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public CartItemResponse build() {
            CartItemResponse c = new CartItemResponse();
            c.setCartItemId(cartItemId);
            c.setMenuItemId(menuItemId);
            c.setItemName(itemName);
            c.setUnitPrice(unitPrice);
            c.setQuantity(quantity);
            c.setSubtotal(subtotal);
            return c;
        }
    }
    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
