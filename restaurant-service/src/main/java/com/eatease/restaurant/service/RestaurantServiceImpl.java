package com.eatease.restaurant.service;

import com.eatease.common.constants.RestaurantStatus;
import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.restaurant.client.UserClient;
import com.eatease.restaurant.dto.*;
import com.eatease.restaurant.entity.MenuItem;
import com.eatease.restaurant.entity.Restaurant;
import com.eatease.restaurant.entity.Review;
import com.eatease.restaurant.repository.MenuItemRepository;
import com.eatease.restaurant.repository.RestaurantRepository;
import com.eatease.restaurant.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final ReviewRepository reviewRepository;
    private final UserClient userClient;

    public RestaurantServiceImpl(
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository,
            ReviewRepository reviewRepository,
            UserClient userClient) {

        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.reviewRepository = reviewRepository;
        this.userClient = userClient;
    }

    @Override
    @Transactional
    public RestaurantResponse create(RestaurantRequest request) {

        Map<String, Object> user = userClient.getUserById(request.getOwnerId());

        Boolean approved = (Boolean) user.get("approved");

        if (approved == null || !approved) {
            throw new BadRequestException("Owner is not approved by admin");
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setOwnerId(request.getOwnerId());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setStatus(RestaurantStatus.PENDING_APPROVAL);

        restaurant = restaurantRepository.save(restaurant);

        return toResponse(restaurant);
    }

    @Override
    public RestaurantResponse getById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
        return toResponseWithMenu(restaurant);
    }

    @Override
    public List<RestaurantResponse> findAll() {
        return restaurantRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> findByStatus(RestaurantStatus status) {
        return restaurantRepository.findByStatus(status).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> findByOwnerId(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId).stream().map(this::toResponseWithMenu).collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> findApprovedByCity(String city) {
        return restaurantRepository.findByCityAndStatus(city, RestaurantStatus.APPROVED)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RestaurantResponse update(Long id, RestaurantRequest request, Long callerUserId) {
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(id, callerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
        if (restaurant.getStatus() == RestaurantStatus.PENDING_APPROVAL || restaurant.getStatus() == RestaurantStatus.APPROVED) {
            restaurant.setName(request.getName());
            restaurant.setDescription(request.getDescription());
            restaurant.setAddress(request.getAddress());
            restaurant.setCity(request.getCity());
            restaurant.setPhone(request.getPhone());
            restaurant.setImageUrl(request.getImageUrl());
            restaurant = restaurantRepository.save(restaurant);
        }
        return toResponse(restaurant);
    }

    @Override
    @Transactional
    public RestaurantResponse approveOrReject(Long id, RestaurantStatus status) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
        if (status != RestaurantStatus.APPROVED && status != RestaurantStatus.REJECTED) {
            throw new BadRequestException("Status must be APPROVED or REJECTED");
        }
        restaurant.setStatus(status);
        restaurant = restaurantRepository.save(restaurant);
        return toResponse(restaurant);
    }

    @Override
    @Transactional
    public MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest request, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(restaurantId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setAvailable(
                request.getAvailable() != null ? request.getAvailable() : true
        );
        item = menuItemRepository.save(item);
        return toMenuItemResponse(item);
    }

    @Override
    public MenuItemResponse getMenuItem(Long menuItemId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", menuItemId));
        return toMenuItemResponse(item);
    }

    @Override
    public List<MenuItemResponse> getMenuByRestaurantId(Long restaurantId) {
        return menuItemRepository.findByRestaurant_Id(restaurantId).stream()
                .map(this::toMenuItemResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long menuItemId, MenuItemRequest request, Long ownerId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", menuItemId));
        if (!item.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("MenuItem", "id", menuItemId);
        }
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        if (request.getAvailable() != null) item.setAvailable(request.getAvailable());
        item = menuItemRepository.save(item);
        return toMenuItemResponse(item);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long menuItemId, Long ownerId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", menuItemId));
        if (!item.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("MenuItem", "id", menuItemId);
        }
        menuItemRepository.delete(item);
    }

    private RestaurantResponse toResponse(Restaurant r) {
        RestaurantResponse resp = new RestaurantResponse();
        resp.setId(r.getId());
        resp.setName(r.getName());
        resp.setDescription(r.getDescription());
        resp.setOwnerId(r.getOwnerId());
        resp.setAddress(r.getAddress());
        resp.setCity(r.getCity());
        resp.setPhone(r.getPhone());
        resp.setImageUrl(r.getImageUrl());
        resp.setStatus(r.getStatus());
        resp.setRating(r.getRating());
        resp.setCreatedAt(r.getCreatedAt());
        return resp;

    }

    private RestaurantResponse toResponseWithMenu(Restaurant r) {
        RestaurantResponse resp = toResponse(r);
        resp.setMenuItems(menuItemRepository.findByRestaurant_Id(r.getId()).stream()
                .map(this::toMenuItemResponse).collect(Collectors.toList()));
        return resp;
    }

    private MenuItemResponse toMenuItemResponse(MenuItem m) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(m.getId());
        response.setRestaurantId(m.getRestaurant().getId());
        response.setName(m.getName());
        response.setDescription(m.getDescription());
        response.setPrice(m.getPrice());
        response.setImageUrl(m.getImageUrl());
        response.setAvailable(m.isAvailable());
        response.setCreatedAt(m.getCreatedAt());
        return response;
    }


    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request, Long customerId) {

        // 1. Fetch restaurant
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId())
                );

        // 2. Create review
        Review review = new Review();
        review.setRestaurant(restaurant);
        review.setCustomerId(customerId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        // 3. Recalculate restaurant rating
        List<Review> reviews =
                reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId());

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);

        restaurant.setRating(BigDecimal.valueOf(averageRating));
        restaurantRepository.save(restaurant);

        // 4. Prepare response
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRestaurantId(restaurant.getId());
        response.setCustomerId(review.getCustomerId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    @Override
    public List<ReviewResponse> getReviewsByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId).stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long callerUserId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        if (!isAdmin && !review.getCustomerId().equals(callerUserId)) {
            throw new BadRequestException("Only the author or admin can delete this review");
        }
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> search(String q, String city) {
        return restaurantRepository.searchApproved(q, city).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse toReviewResponse(Review r) {
        ReviewResponse response = new ReviewResponse();
        response.setId(r.getId());
        response.setRestaurantId(r.getRestaurant().getId());
        response.setCustomerId(r.getCustomerId());
        response.setRating(r.getRating());
        response.setComment(r.getComment());
        response.setCreatedAt(r.getCreatedAt());
        return response;
    }

}
