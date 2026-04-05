package com.eatease.restaurant.controller;

import com.eatease.common.constants.RestaurantStatus;
import com.eatease.restaurant.dto.*;
import com.eatease.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // ================= CREATE RESTAURANT =================

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> create(
            @Valid @RequestBody RestaurantRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        request.setOwnerId(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.create(request));
    }

    // ================= GET =================

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> findAll(
            @RequestParam(required = false) RestaurantStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String q) {

        if (q != null && !q.isBlank())
            return ResponseEntity.ok(restaurantService.search(q, city));

        if (city != null && !city.isBlank())
            return ResponseEntity.ok(restaurantService.findApprovedByCity(city));

        if (ownerId != null)
            return ResponseEntity.ok(restaurantService.findByOwnerId(ownerId));

        if (status != null)
            return ResponseEntity.ok(restaurantService.findByStatus(status));

        return ResponseEntity.ok(restaurantService.findAll());
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(
                restaurantService.update(id, request, userId)
        );
    }

    // ================= ADMIN APPROVE =================

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> approveOrReject(
            @PathVariable Long id,
            @RequestParam RestaurantStatus status) {

        return ResponseEntity.ok(
                restaurantService.approveOrReject(id, status)
        );
    }

    // ================= MENU =================

    @PostMapping("/{restaurantId}/menus")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.addMenuItem(restaurantId, request, userId));
    }

    @DeleteMapping("/menus/{menuItemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long menuItemId,
            @RequestHeader("X-User-Id") Long userId) {

        restaurantService.deleteMenuItem(menuItemId, userId);
        return ResponseEntity.noContent().build();
    }

    // ================= REVIEWS =================

    @PostMapping("/reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> addReview(
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.addReview(request, userId));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("X-User-Id") Long userId) {

        restaurantService.deleteReview(reviewId, userId, false);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{restaurantId}/menus")
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @PathVariable Long restaurantId) {

        return ResponseEntity.ok(
                restaurantService.getMenuByRestaurantId(restaurantId)
        );
    }


    @GetMapping("/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(restaurantService.getAllReviews());
    }
}
