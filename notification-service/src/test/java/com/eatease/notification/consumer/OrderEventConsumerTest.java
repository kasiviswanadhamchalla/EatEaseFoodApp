package com.eatease.notification.consumer;

import com.eatease.notification.event.NotificationEventPayload;
import com.eatease.notification.event.OrderEventPayload;
import com.eatease.notification.producer.NotificationEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderEventConsumerTest {

    private DummyNotificationEventProducer dummyProducer;
    private OrderEventConsumer orderEventConsumer;

    static class DummyNotificationEventProducer extends NotificationEventProducer {
        List<NotificationEventPayload> publishedPayloads = new ArrayList<>();

        public DummyNotificationEventProducer() {
            super(null);
        }

        @Override
        public void publish(NotificationEventPayload payload) {
            publishedPayloads.add(payload);
        }
    }

    @BeforeEach
    void setUp() {
        dummyProducer = new DummyNotificationEventProducer();
        orderEventConsumer = new OrderEventConsumer(dummyProducer);
    }

    @Test
    void shouldConsumeAndPublishCustomerNotificationWhenNotPending() {
        OrderEventPayload payload = new OrderEventPayload();
        payload.setOrderId(1L);
        payload.setOrderNumber("ORD-123");
        payload.setCustomerId(5L);
        payload.setStatus("CONFIRMED");

        orderEventConsumer.consume(payload);

        assertEquals(1, dummyProducer.publishedPayloads.size());
        NotificationEventPayload notification = dummyProducer.publishedPayloads.get(0);
        
        assertEquals(5L, notification.getUserId());
        assertEquals("ORDER_STATUS", notification.getType());
        assertEquals("Your order is confirmed.", notification.getBody());
    }

    @Test
    void shouldConsumeAndPublishBothNotificationsWhenPending() {
        OrderEventPayload payload = new OrderEventPayload();
        payload.setOrderId(2L);
        payload.setOrderNumber("ORD-999");
        payload.setCustomerId(10L);
        payload.setRestaurantId(20L);
        payload.setTotalAmount(BigDecimal.valueOf(50.0));
        payload.setStatus("PENDING");

        orderEventConsumer.consume(payload);

        assertEquals(2, dummyProducer.publishedPayloads.size());
        
        NotificationEventPayload custNotif = dummyProducer.publishedPayloads.get(0);
        assertEquals(10L, custNotif.getUserId());
        assertEquals("ORDER_STATUS", custNotif.getType());

        NotificationEventPayload restNotif = dummyProducer.publishedPayloads.get(1);
        assertEquals(20L, restNotif.getUserId());
        assertEquals("NEW_ORDER", restNotif.getType());
    }
}
