package com.eatease.notification.consumer;

import com.eatease.notification.document.NotificationDoc;
import com.eatease.notification.event.NotificationEventPayload;
import com.eatease.notification.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private final NotificationRepository notificationRepository;

    public NotificationEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "notification-events", groupId = "notification-persistence-group")
    public void consume(NotificationEventPayload event) {
        NotificationDoc notification =
                NotificationDoc.create(
                        event.getUserId(),
                        event.getType(),
                        event.getTitle(),
                        event.getBody(),
                        event.getReferenceId()
                );
        notificationRepository.save(notification);
    }
}
