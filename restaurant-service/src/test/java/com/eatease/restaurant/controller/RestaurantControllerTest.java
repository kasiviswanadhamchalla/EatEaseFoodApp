package com.eatease.restaurant.controller;

import com.eatease.common.constants.RestaurantStatus;
import com.eatease.restaurant.dto.RestaurantRequest;
import com.eatease.restaurant.dto.RestaurantResponse;
import com.eatease.restaurant.service.RestaurantService;
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
class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController controller;

    @Test
    void createShouldReturnCreatedRestaurant() {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Test Restaurant");
        
        RestaurantResponse expected = new RestaurantResponse();
        expected.setId(1L);
        expected.setName("Test Restaurant");
        
        when(restaurantService.create(request)).thenReturn(expected);

        ResponseEntity<RestaurantResponse> response = controller.create(request, 10L);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
        assertEquals(10L, request.getOwnerId());
    }

    @Test
    void getByIdShouldReturnRestaurant() {
        RestaurantResponse expected = new RestaurantResponse();
        expected.setId(1L);
        when(restaurantService.getById(1L)).thenReturn(expected);

        ResponseEntity<RestaurantResponse> response = controller.getById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void findAllWithoutParamsShouldReturnAll() {
        List<RestaurantResponse> expected = List.of(new RestaurantResponse());
        when(restaurantService.findAll()).thenReturn(expected);

        ResponseEntity<List<RestaurantResponse>> response = controller.findAll(null, null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
}
