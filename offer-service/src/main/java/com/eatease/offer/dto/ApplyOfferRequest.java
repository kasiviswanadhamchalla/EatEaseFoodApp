package com.eatease.offer.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ApplyOfferRequest {
    @NotNull
    private String code;
    @NotNull
    private BigDecimal orderAmount;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }
}
