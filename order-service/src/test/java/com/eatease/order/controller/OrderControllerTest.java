package com.eatease.order.controller;

import com.eatease.order.dto.OrderResponse;
import com.eatease.order.dto.PlaceOrderRequest;
import com.eatease.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController controller;

    @Test
    void placeOrderShouldReturnCreated() {
        PlaceOrderRequest request = new PlaceOrderRequest();
        OrderResponse expected = new OrderResponse();
        expected.setId(1L);
        
        when(orderService.placeOrder(10L, request)).thenReturn(expected);

        ResponseEntity<OrderResponse> response = controller.placeOrder(10L, request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getOrderShouldReturnOkForCustomer() {
        OrderResponse expected = new OrderResponse();
        when(orderService.getOrder(5L, 10L, false)).thenReturn(expected);

        ResponseEntity<OrderResponse> response = controller.getOrder(5L, 10L, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
    
    @Test
    void restaurantOrdersShouldReturnOk() {
        List<OrderResponse> expected = List.of(new OrderResponse());
        when(orderService.getOrdersByRestaurant(20L)).thenReturn(expected);

        ResponseEntity<List<OrderResponse>> response = controller.restaurantOrders(20L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
}
