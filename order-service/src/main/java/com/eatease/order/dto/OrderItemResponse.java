package com.eatease.order.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long menuItemId;
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public OrderItemResponse() { }
    public static OrderItemResponseBuilder builder() { return new OrderItemResponseBuilder(); }
    public static class OrderItemResponseBuilder {
        private Long menuItemId;
        private String itemName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        public OrderItemResponseBuilder menuItemId(Long menuItemId) { this.menuItemId = menuItemId; return this; }
        public OrderItemResponseBuilder itemName(String itemName) { this.itemName = itemName; return this; }
        public OrderItemResponseBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public OrderItemResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public OrderItemResponseBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public OrderItemResponse build() {
            OrderItemResponse o = new OrderItemResponse();
            o.setMenuItemId(menuItemId);
            o.setItemName(itemName);
            o.setUnitPrice(unitPrice);
            o.setQuantity(quantity);
            o.setSubtotal(subtotal);
            return o;
        }
    }
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
