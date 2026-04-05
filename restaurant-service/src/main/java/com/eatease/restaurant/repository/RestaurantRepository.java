package com.eatease.restaurant.repository;

import com.eatease.common.constants.RestaurantStatus;
import com.eatease.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByStatus(RestaurantStatus status);

    List<Restaurant> findByOwnerId(Long ownerId);

    List<Restaurant> findByCityAndStatus(String city, RestaurantStatus status);

    Optional<Restaurant> findByIdAndOwnerId(Long id, Long ownerId);

    List<Restaurant> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String desc);

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND (:city IS NULL OR LOWER(r.city) = LOWER(:city)) AND (:q IS NULL OR :q = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Restaurant> searchApproved(@Param("q") String q, @Param("city") String city);
}
