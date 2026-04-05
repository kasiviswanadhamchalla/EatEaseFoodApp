package com.eatease.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "restaurant-service")
public interface RestaurantServiceClient {

    @GetMapping("/api/restaurants")
    List<Map<String, Object>> findAll();

    @GetMapping("/api/reviews/admin/all")
    List<Map<String, Object>> getAllReviews();
}
