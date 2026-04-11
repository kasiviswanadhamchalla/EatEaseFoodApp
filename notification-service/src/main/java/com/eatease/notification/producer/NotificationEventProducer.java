package com.eatease.notification.producer;

import com.eatease.notification.event.NotificationEventPayload;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventProducer {

    private final KafkaTemplate<String, NotificationEventPayload> kafkaTemplate;

    public NotificationEventProducer(KafkaTemplate<String, NotificationEventPayload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(NotificationEventPayload payload) {
        String key = payload.getUserId() == null ? null : payload.getUserId().toString();
        kafkaTemplate.send("notification-events", key, payload);
    }
}
