package com.eatease.order.service;

import com.eatease.common.constants.OrderStatus;
import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.order.client.MenuItemDto;
import com.eatease.order.client.RestaurantClient;
import com.eatease.order.dto.*;
import com.eatease.order.entity.*;
import com.eatease.order.repository.CartRepository;
import com.eatease.order.repository.OrderRepository;
import com.eatease.order.event.OrderEventPayload;
import com.eatease.order.producer.OrderEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;
    private final OrderEventProducer orderEventProducer;

    public OrderServiceImpl(CartRepository cartRepository,
                            OrderRepository orderRepository,
                            RestaurantClient restaurantClient,
                            OrderEventProducer orderEventProducer) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.restaurantClient = restaurantClient;
        this.orderEventProducer = orderEventProducer;
    }

    // ================= ADD TO CART =================
    @Override
    @Transactional
    public CartResponse addToCart(Long customerId, CartItemRequest request) {

        MenuItemDto menuItem = restaurantClient.getMenuItem(request.getMenuItemId());

        if (menuItem == null || !menuItem.isAvailable()) {
            throw new BadRequestException("Menu item not available");
        }

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCustomerId(customerId);
                    return cartRepository.save(c);
                });

        if (cart.getRestaurantId() != null &&
                !cart.getRestaurantId().equals(menuItem.getRestaurantId())) {
            throw new BadRequestException("Cart already has items from another restaurant");
        }

        cart.setRestaurantId(menuItem.getRestaurantId());

        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getMenuItemId().equals(menuItem.getId())) {
                item.setQuantity(item.getQuantity() + request.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setMenuItemId(menuItem.getId());
            item.setItemName(menuItem.getName());
            item.setUnitPrice(menuItem.getPrice());
            item.setQuantity(request.getQuantity());
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
        return toCartResponse(cart);
    }

    // ================= GET CART =================
    @Override
    public CartResponse getCart(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCustomerId(customerId);
                    return c;
                });
        return toCartResponse(cart);
    }

    // ================= UPDATE CART ITEM =================
    @Override
    @Transactional
    public CartResponse updateCartItem(Long customerId, Long cartItemId, int quantity) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "customerId", customerId));

        CartItem target = null;
        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(cartItemId)) {
                target = item;
                break;
            }
        }

        if (target == null) {
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        if (quantity <= 0) {
            cart.getItems().remove(target);
        } else {
            target.setQuantity(quantity);
        }

        if (cart.getItems().isEmpty()) {
            cart.setRestaurantId(null);
        }

        cartRepository.save(cart);
        return toCartResponse(cart);
    }

    // ================= CLEAR CART =================
    @Override
    @Transactional
    public void clearCart(Long customerId) {
        cartRepository.findByCustomerId(customerId).ifPresent(cart -> {
            cart.getItems().clear();
            cart.setRestaurantId(null);
            cartRepository.save(cart);
        });
    }

    // ================= PLACE ORDER =================
    @Override
    @Transactional
    public OrderResponse placeOrder(Long customerId, PlaceOrderRequest request) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "customerId", customerId));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        order.setCustomerId(customerId);
        order.setRestaurantId(cart.getRestaurantId());
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setCreatedAt(Instant.now());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            BigDecimal subtotal = ci.getUnitPrice()
                    .multiply(BigDecimal.valueOf(ci.getQuantity()));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setMenuItemId(ci.getMenuItemId());
            oi.setItemName(ci.getItemName());
            oi.setUnitPrice(ci.getUnitPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setSubtotal(subtotal);

            order.getItems().add(oi);
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);
        orderRepository.save(order);

        cart.getItems().clear();
        cart.setRestaurantId(null);
        cartRepository.save(cart);

        List<OrderEventPayload.OrderItemPayload> eventItems = order.getItems().stream()
                .map(i -> new OrderEventPayload.OrderItemPayload(i.getMenuItemId(), i.getItemName(), i.getQuantity(), i.getSubtotal()))
                .collect(Collectors.toList());

        OrderEventPayload event = new OrderEventPayload(
                order.getId(), order.getOrderNumber(), order.getCustomerId(), order.getRestaurantId(),
                order.getStatus().name(), order.getDeliveryAddress(), order.getTotalAmount(), order.getCreatedAt(), eventItems
        );
        orderEventProducer.publish(event);

        return toOrderResponse(order);
    }

    // ================= GET ORDER =================
    @Override
    public OrderResponse getOrder(Long orderId, Long callerUserId, boolean isRestaurant) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!isRestaurant && !order.getCustomerId().equals(callerUserId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        if (isRestaurant && !order.getRestaurantId().equals(callerUserId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        return toOrderResponse(order);
    }

    // ================= LIST APIs =================
    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        List<OrderResponse> list = new ArrayList<>();
        for (OrderEntity o : orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)) {
            list.add(toOrderResponse(o));
        }
        return list;
    }

    @Override
    public List<OrderResponse> getOrdersByRestaurant(Long restaurantId) {
        List<OrderResponse> list = new ArrayList<>();
        for (OrderEntity o : orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)) {
            list.add(toOrderResponse(o));
        }
        return list;
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status, Long restaurantId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        order.setStatus(status);
        orderRepository.save(order);

        List<OrderEventPayload.OrderItemPayload> eventItems = order.getItems().stream()
                .map(i -> new OrderEventPayload.OrderItemPayload(i.getMenuItemId(), i.getItemName(), i.getQuantity(), i.getSubtotal()))
                .collect(Collectors.toList());

        OrderEventPayload event = new OrderEventPayload(
                order.getId(), order.getOrderNumber(), order.getCustomerId(), order.getRestaurantId(),
                order.getStatus().name(), order.getDeliveryAddress(), order.getTotalAmount(), order.getCreatedAt(), eventItems
        );
        orderEventProducer.publish(event);

        return toOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<OrderResponse> list = new ArrayList<>();
        for (OrderEntity o : orderRepository.findAll()) {
            list.add(toOrderResponse(o));
        }
        return list;
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        List<OrderResponse> list = new ArrayList<>();
        for (OrderEntity o : orderRepository.findByStatus(status)) {
            list.add(toOrderResponse(o));
        }
        return list;
    }

    // ================= MAPPERS =================
    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            BigDecimal subtotal =
                    ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));

            CartItemResponse r = new CartItemResponse();
            r.setCartItemId(ci.getId());
            r.setMenuItemId(ci.getMenuItemId());
            r.setItemName(ci.getItemName());
            r.setUnitPrice(ci.getUnitPrice());
            r.setQuantity(ci.getQuantity());
            r.setSubtotal(subtotal);

            items.add(r);
            total = total.add(subtotal);
        }

        CartResponse res = new CartResponse();
        res.setCartId(cart.getId());
        res.setCustomerId(cart.getCustomerId());
        res.setRestaurantId(cart.getRestaurantId());
        res.setItems(items);
        res.setTotalAmount(total);
        return res;
    }

    private OrderResponse toOrderResponse(OrderEntity o) {

        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderItem i : o.getItems()) {
            OrderItemResponse r = new OrderItemResponse();
            r.setMenuItemId(i.getMenuItemId());
            r.setItemName(i.getItemName());
            r.setUnitPrice(i.getUnitPrice());
            r.setQuantity(i.getQuantity());
            r.setSubtotal(i.getSubtotal());
            items.add(r);
        }

        OrderResponse res = new OrderResponse();
        res.setId(o.getId());
        res.setOrderNumber(o.getOrderNumber());
        res.setCustomerId(o.getCustomerId());
        res.setRestaurantId(o.getRestaurantId());
        res.setStatus(o.getStatus());
        res.setDeliveryAddress(o.getDeliveryAddress());
        res.setTotalAmount(o.getTotalAmount());
        res.setDeliveryPartnerId(o.getDeliveryPartnerId());
        res.setCreatedAt(o.getCreatedAt());
        res.setUpdatedAt(o.getUpdatedAt());
        res.setItems(items);
        return res;
    }
}
