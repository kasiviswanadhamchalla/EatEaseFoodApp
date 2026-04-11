package com.eatease.order.service;

import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.order.client.RestaurantClient;
import com.eatease.order.dto.CartResponse;
import com.eatease.order.entity.Cart;
import com.eatease.order.entity.OrderEntity;
import com.eatease.order.repository.CartRepository;
import com.eatease.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RestaurantClient restaurantClient;
    @Mock
    private com.eatease.order.producer.OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getCartShouldCreateNewCartIfNotFound() {
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.empty());

        CartResponse response = orderService.getCart(5L);

        assertNotNull(response);
        assertEquals(5L, response.getCustomerId());
    }

    @Test
    void getOrderShouldThrowIfNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder(99L, 1L, false));
    }
    
    @Test
    void getOrderShouldReturnOrderIfCustomerMatches() {
        OrderEntity order = new OrderEntity();
        order.setId(10L);
        order.setCustomerId(5L);
        
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        
        var response = orderService.getOrder(10L, 5L, false);
        
        assertEquals(10L, response.getId());
    }
    
    @Test
    void clearCartShouldClearItems() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCustomerId(5L);
        cart.setRestaurantId(20L);
        when(cartRepository.findByCustomerId(5L)).thenReturn(Optional.of(cart));
        
        orderService.clearCart(5L);
        
        assertNull(cart.getRestaurantId());
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }
}
