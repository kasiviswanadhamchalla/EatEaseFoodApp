package com.eatease.search.controller;

import com.eatease.search.dto.RestaurantSearchResponse;
import com.eatease.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController controller;

    @Test
    void searchRestaurantsShouldReturnListOfRestaurants() {
        List<RestaurantSearchResponse> expected = List.of(new RestaurantSearchResponse());
        when(searchService.searchRestaurants("Pizza", "NY")).thenReturn(expected);

        ResponseEntity<List<RestaurantSearchResponse>> response = controller.searchRestaurants("Pizza", "NY");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void searchRestaurantsWithoutParamsShouldReturnAll() {
        List<RestaurantSearchResponse> expected = List.of(new RestaurantSearchResponse());
        when(searchService.searchRestaurants(null, null)).thenReturn(expected);

        ResponseEntity<List<RestaurantSearchResponse>> response = controller.searchRestaurants(null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
}
