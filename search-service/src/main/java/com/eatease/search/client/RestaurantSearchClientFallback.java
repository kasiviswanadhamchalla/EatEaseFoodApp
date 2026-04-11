package com.eatease.search.client;

import com.eatease.search.dto.RestaurantSearchResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RestaurantSearchClientFallback implements RestaurantSearchClient {

    @Override
    public List<RestaurantSearchResponse> search(String q, String city) {
        // Return an empty list as a fallback when restaurant service is down.
        return Collections.emptyList();
    }
}
