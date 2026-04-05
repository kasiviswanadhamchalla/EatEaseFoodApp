package com.eatease.restaurant.dto;

import com.eatease.common.constants.RestaurantStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class RestaurantResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String address;
    private String city;
    private String phone;
    private String imageUrl;
    private RestaurantStatus status;
    private BigDecimal rating;
    private Instant createdAt;
    private List<MenuItemResponse> menuItems;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public RestaurantStatus getStatus() { return status; }
    public void setStatus(RestaurantStatus status) { this.status = status; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<MenuItemResponse> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItemResponse> menuItems) { this.menuItems = menuItems; }
}
