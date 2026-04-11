package com.eatease.admin.controller;

import com.eatease.admin.client.OrderServiceClient;
import com.eatease.admin.client.PaymentServiceClient;
import com.eatease.admin.client.RestaurantServiceClient;
import com.eatease.admin.client.UserServiceClient;
import com.eatease.admin.dto.DashboardSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private UserServiceClient userClient;

    @Mock
    private RestaurantServiceClient restaurantClient;

    @Mock
    private OrderServiceClient orderClient;

    @Mock
    private PaymentServiceClient paymentClient;

    @InjectMocks
    private AdminDashboardController controller;

    @Test
    void dashboardShouldThrowIfNotAdmin() {
        assertThrows(ResponseStatusException.class, () -> controller.dashboard("token", "CUSTOMER"));
    }

    @Test
    void dashboardShouldReturnSummaryForAdmin() {
        when(userClient.getAllUsers("token", "ADMIN,CUSTOMER")).thenReturn(List.of(Map.of(), Map.of()));
        when(restaurantClient.findAll()).thenReturn(List.of(Map.of()));
        when(orderClient.getAllOrders("token", "ADMIN,CUSTOMER")).thenReturn(List.of(Map.of(), Map.of(), Map.of()));
        when(paymentClient.getAllPayments("token", "ADMIN,CUSTOMER")).thenReturn(List.of(Map.of()));
        when(restaurantClient.getAllReviews()).thenReturn(List.of());

        DashboardSummary summary = controller.dashboard("token", "ADMIN,CUSTOMER");

        assertEquals(2, summary.getTotalUsers());
        assertEquals(0, summary.getPendingApprovals());
        assertEquals(1, summary.getTotalRestaurants());
        assertEquals(3, summary.getTotalOrders());
        assertEquals(1, summary.getTotalPayments());
        assertEquals(0, summary.getTotalReviews());
    }
}
