package com.eatease.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/menus/{menuItemId}")
    MenuItemDto getMenuItem(@PathVariable("menuItemId") Long menuItemId);
}
