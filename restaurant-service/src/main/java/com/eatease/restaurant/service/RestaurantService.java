package com.eatease.restaurant.service;

import com.eatease.common.constants.RestaurantStatus;
import com.eatease.restaurant.dto.*;

import java.util.List;

public interface RestaurantService {
    RestaurantResponse create(RestaurantRequest request);
    RestaurantResponse getById(Long id);
    List<RestaurantResponse> findAll();
    List<RestaurantResponse> findByStatus(RestaurantStatus status);
    List<RestaurantResponse> findByOwnerId(Long ownerId);
    List<RestaurantResponse> findApprovedByCity(String city);
    RestaurantResponse update(Long id, RestaurantRequest request, Long callerUserId);
    RestaurantResponse approveOrReject(Long id, RestaurantStatus status);
    MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest request, Long ownerId);
    MenuItemResponse getMenuItem(Long menuItemId);
    List<MenuItemResponse> getMenuByRestaurantId(Long restaurantId);
    MenuItemResponse updateMenuItem(Long menuItemId, MenuItemRequest request, Long ownerId);
    void deleteMenuItem(Long menuItemId, Long ownerId);
    ReviewResponse addReview(ReviewRequest request, Long customerId);
    List<ReviewResponse> getReviewsByRestaurant(Long restaurantId);
    void deleteReview(Long reviewId, Long callerUserId, boolean isAdmin);
    List<ReviewResponse> getAllReviews();
    List<RestaurantResponse> search(String q, String city);
}
