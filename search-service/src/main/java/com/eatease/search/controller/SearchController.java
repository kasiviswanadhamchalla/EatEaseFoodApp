package com.eatease.search.controller;

import com.eatease.search.dto.RestaurantSearchResponse;
import com.eatease.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantSearchResponse>> searchRestaurants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city) {

        return ResponseEntity.ok(searchService.searchRestaurants(q, city));
    }
}

