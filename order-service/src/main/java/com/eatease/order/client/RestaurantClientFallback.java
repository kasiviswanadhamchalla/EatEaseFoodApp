package com.eatease.order.client;

import org.springframework.stereotype.Component;

@Component
public class RestaurantClientFallback implements RestaurantClient {

    @Override
    public MenuItemDto getMenuItem(Long menuItemId) {
        throw new RuntimeException("Restaurant Service is currently unavailable. Cannot fetch menu item details for order.");
    }
}
