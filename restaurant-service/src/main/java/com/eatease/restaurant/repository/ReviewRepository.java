package com.eatease.restaurant.repository;

import com.eatease.restaurant.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    List<Review> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
