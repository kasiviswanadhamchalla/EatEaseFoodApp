package com.eatease.admin.controller;

import com.eatease.admin.client.*;
import com.eatease.admin.dto.DashboardSummary;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final UserServiceClient userClient;
    private final RestaurantServiceClient restaurantClient;
    private final OrderServiceClient orderClient;
    private final PaymentServiceClient paymentClient;

    public AdminDashboardController(
            UserServiceClient userClient,
            RestaurantServiceClient restaurantClient,
            OrderServiceClient orderClient,
            PaymentServiceClient paymentClient) {
        this.userClient = userClient;
        this.restaurantClient = restaurantClient;
        this.orderClient = orderClient;
        this.paymentClient = paymentClient;
    }

    @GetMapping("/dashboard")
    public DashboardSummary dashboard(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Roles") String roles) {

        if (!roles.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }

        DashboardSummary dto = new DashboardSummary();

        dto.setTotalUsers(userClient.getAllUsers(token, roles).size());
        dto.setPendingApprovals(0);
        dto.setTotalRestaurants(restaurantClient.findAll().size());
        dto.setTotalOrders(orderClient.getAllOrders(token, roles).size());
        dto.setTotalPayments(paymentClient.getAllPayments(token, roles).size());
        dto.setTotalReviews(restaurantClient.getAllReviews().size());

        return dto;
    }
}
