package com.eatease.search.service;

import com.eatease.search.client.RestaurantSearchClient;
import com.eatease.search.dto.RestaurantSearchResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final RestaurantSearchClient restaurantSearchClient;

    public SearchService(RestaurantSearchClient restaurantSearchClient) {
        this.restaurantSearchClient = restaurantSearchClient;
    }

    public List<RestaurantSearchResponse> searchRestaurants(String q, String city) {
        return restaurantSearchClient.search(q, city);
    }
}
