package com.eatease.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @GetMapping("/api/payments/admin/all")
    List<Map<String, Object>> getAllPayments(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Roles") String roles
    );
}
