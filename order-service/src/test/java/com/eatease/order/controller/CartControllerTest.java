package com.eatease.order.controller;

import com.eatease.order.dto.CartItemRequest;
import com.eatease.order.dto.CartResponse;
import com.eatease.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CartController controller;

    @Test
    void getCartShouldReturnOk() {
        CartResponse expected = new CartResponse();
        expected.setCartId(1L);
        when(orderService.getCart(100L)).thenReturn(expected);

        ResponseEntity<CartResponse> response = controller.getCart(100L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void addItemShouldReturnCartResponse() {
        CartItemRequest request = new CartItemRequest();
        CartResponse expected = new CartResponse();
        when(orderService.addToCart(100L, request)).thenReturn(expected);

        ResponseEntity<CartResponse> response = controller.addItem(100L, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void clearCartShouldReturnNoContent() {
        ResponseEntity<Void> response = controller.clearCart(100L);

        assertEquals(204, response.getStatusCode().value());
        verify(orderService).clearCart(100L);
    }
}
