package com.eatease.search.dto;


import com.eatease.common.constants.RestaurantStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class RestaurantSearchResponse {

    private Long id;
    private String name;
    private String description;
    private String city;
    private BigDecimal rating;
    private RestaurantStatus status;
    private Instant createdAt;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public RestaurantStatus getStatus() { return status; }
    public void setStatus(RestaurantStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
