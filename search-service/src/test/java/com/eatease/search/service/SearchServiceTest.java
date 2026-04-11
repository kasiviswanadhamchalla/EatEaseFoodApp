package com.eatease.search.service;

import com.eatease.search.client.RestaurantSearchClient;
import com.eatease.search.dto.RestaurantSearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private RestaurantSearchClient restaurantSearchClient;

    @InjectMocks
    private SearchService searchService;

    @Test
    void searchRestaurantsShouldInvokeClientAndReturnList() {
        List<RestaurantSearchResponse> expected = List.of(new RestaurantSearchResponse());
        when(restaurantSearchClient.search("Pasta", "Rome")).thenReturn(expected);

        List<RestaurantSearchResponse> response = searchService.searchRestaurants("Pasta", "Rome");

        assertEquals(expected, response);
    }
}
