package com.eatease.restaurant.repository;

import com.eatease.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurant_Id(Long restaurantId);

    List<MenuItem> findByRestaurant_IdAndAvailable(Long restaurantId, boolean available);

}
