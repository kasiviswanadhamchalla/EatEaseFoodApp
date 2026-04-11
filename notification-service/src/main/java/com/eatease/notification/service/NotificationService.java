package com.eatease.notification.service;

import com.eatease.notification.dto.NotificationResponse;
import com.eatease.notification.event.NotificationEventPayload;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getByUserId(Long userId, int page, int size);
    long getUnreadCount(Long userId);
    void publish(NotificationEventPayload event);
}
