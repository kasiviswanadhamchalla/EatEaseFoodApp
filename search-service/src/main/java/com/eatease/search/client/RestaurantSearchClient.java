package com.eatease.search.client;

import com.eatease.search.dto.RestaurantSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "restaurant-service", fallback = RestaurantSearchClientFallback.class)
public interface RestaurantSearchClient {

    @GetMapping("/api/restaurants/search")
    List<RestaurantSearchResponse> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "city", required = false) String city
    );
}
