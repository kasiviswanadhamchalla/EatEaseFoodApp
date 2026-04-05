package com.eatease.order.dto;

import jakarta.validation.constraints.NotBlank;

public class PlaceOrderRequest {
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}
