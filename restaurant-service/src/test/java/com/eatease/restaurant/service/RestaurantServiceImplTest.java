package com.eatease.restaurant.service;

import com.eatease.common.constants.RestaurantStatus;
import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.restaurant.client.UserClient;
import com.eatease.restaurant.dto.RestaurantRequest;
import com.eatease.restaurant.dto.RestaurantResponse;
import com.eatease.restaurant.entity.Restaurant;
import com.eatease.restaurant.repository.MenuItemRepository;
import com.eatease.restaurant.repository.RestaurantRepository;
import com.eatease.restaurant.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Test
    void createShouldThrowIfOwnerNotApproved() {
        RestaurantRequest request = new RestaurantRequest();
        request.setOwnerId(5L);
        when(userClient.getUserById(5L)).thenReturn(Map.of("approved", false));

        assertThrows(BadRequestException.class, () -> restaurantService.create(request));
    }

    @Test
    void createShouldSaveAndReturnRestaurantIfApproved() {
        RestaurantRequest request = new RestaurantRequest();
        request.setOwnerId(5L);
        request.setName("New Place");
        
        when(userClient.getUserById(5L)).thenReturn(Map.of("approved", true));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(i -> {
            Restaurant r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        RestaurantResponse response = restaurantService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Place", response.getName());
        assertEquals(RestaurantStatus.PENDING_APPROVAL, response.getStatus());
    }

    @Test
    void getByIdShouldThrowIfNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.getById(99L));
    }

    @Test
    void updateShouldUpdateFieldsWhenValid() {
        Restaurant existing = new Restaurant();
        existing.setId(10L);
        existing.setOwnerId(5L);
        existing.setStatus(RestaurantStatus.APPROVED);

        RestaurantRequest request = new RestaurantRequest();
        request.setName("Updated Name");

        when(restaurantRepository.findByIdAndOwnerId(10L, 5L)).thenReturn(Optional.of(existing));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(i -> i.getArgument(0));

        RestaurantResponse response = restaurantService.update(10L, request, 5L);

        assertEquals("Updated Name", response.getName());
    }
}
