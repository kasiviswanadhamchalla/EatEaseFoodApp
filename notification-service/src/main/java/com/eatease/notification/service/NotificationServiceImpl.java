package com.eatease.notification.service;

import com.eatease.notification.document.NotificationDoc;
import com.eatease.notification.dto.NotificationResponse;
import com.eatease.notification.event.NotificationEventPayload;
import com.eatease.notification.producer.NotificationEventProducer;
import com.eatease.notification.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationEventProducer notificationEventProducer;

    public NotificationServiceImpl(NotificationRepository repository,
                                   NotificationEventProducer notificationEventProducer) {
        this.repository = repository;
        this.notificationEventProducer = notificationEventProducer;
    }

    @Override
    public List<NotificationResponse> getByUserId(Long userId, int page, int size) {

        List<NotificationDoc> docs =
                repository.findByUserIdOrderByCreatedAtDesc(
                        userId,
                        PageRequest.of(page, size)
                );

        List<NotificationResponse> responseList = new ArrayList<>();

        for (NotificationDoc doc : docs) {
            NotificationResponse r = new NotificationResponse();
            r.setId(doc.getId());
            r.setUserId(doc.getUserId());
            r.setType(doc.getType());
            r.setTitle(doc.getTitle());
            r.setBody(doc.getBody());
            r.setReferenceId(doc.getReferenceId());
            r.setRead(doc.isRead());
            r.setCreatedAt(doc.getCreatedAt());
            responseList.add(r);
        }

        return responseList;
    }

    @Override
    public long getUnreadCount(Long userId) {
        return repository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public void publish(NotificationEventPayload event) {
        notificationEventProducer.publish(event);
    }
}
