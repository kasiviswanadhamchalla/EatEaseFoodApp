package com.eatease.notification.consumer;

import com.eatease.notification.event.NotificationEventPayload;
import com.eatease.notification.event.OrderEventPayload;
import com.eatease.notification.producer.NotificationEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventConsumer.class);

    private final NotificationEventProducer notificationEventProducer;

    public OrderEventConsumer(NotificationEventProducer notificationEventProducer) {
        this.notificationEventProducer = notificationEventProducer;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void consume(OrderEventPayload event) {

        log.info("Order event received: {}", event.getOrderNumber());

        String status = event.getStatus() != null
                ? event.getStatus().toUpperCase()
                : "";

        // ---------- Customer Notification ----------
        String message = getCustomerMessage(status);

        NotificationEventPayload customerNotification =
                new NotificationEventPayload(
                        event.getCustomerId(),
                        "ORDER_STATUS",
                        "Order " + event.getOrderNumber(),
                        message,
                        event.getOrderId().toString()
                );

        notificationEventProducer.publish(customerNotification);

        // ---------- Restaurant Notification ----------
        if ("PENDING".equals(status)) {

            NotificationEventPayload restaurantNotification =
                    new NotificationEventPayload(
                            event.getRestaurantId(),
                            "NEW_ORDER",
                            "New Order " + event.getOrderNumber(),
                            "You received a new order. Total: " + event.getTotalAmount(),
                            event.getOrderId().toString()
                    );

            notificationEventProducer.publish(restaurantNotification);
        }
    }

    private String getCustomerMessage(String status) {
        return switch (status) {
            case "PENDING" -> "Your order has been placed.";
            case "CONFIRMED" -> "Your order is confirmed.";
            case "PREPARING" -> "Your food is being prepared.";
            case "READY" -> "Your order is ready.";
            case "OUT_FOR_DELIVERY" -> "Your order is on the way.";
            case "DELIVERED" -> "Order delivered successfully.";
            case "CANCELLED" -> "Your order was cancelled.";
            default -> "Order status updated.";
        };
    }
}
