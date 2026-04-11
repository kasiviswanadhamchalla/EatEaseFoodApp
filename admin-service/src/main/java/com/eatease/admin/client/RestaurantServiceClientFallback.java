package com.eatease.admin.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RestaurantServiceClientFallback implements RestaurantServiceClient {

    @Override
    public List<Map<String, Object>> findAll() {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getAllReviews() {
        return Collections.emptyList();
    }
}
