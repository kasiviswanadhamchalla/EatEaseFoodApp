package com.eatease.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/orders/admin/all")
    List<Map<String, Object>> getAllOrders(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Roles") String roles
    );
}
