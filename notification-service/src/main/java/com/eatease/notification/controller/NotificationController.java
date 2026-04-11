package com.eatease.notification.controller;

import com.eatease.notification.dto.NotificationPublishRequest;
import com.eatease.notification.dto.NotificationResponse;
import com.eatease.notification.event.NotificationEventPayload;
import com.eatease.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getByUserId(userId, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(userId)));
    }

    @PostMapping("/publish")
    public ResponseEntity<Map<String, String>> publishAsync(@Valid @RequestBody NotificationPublishRequest request) {
        NotificationEventPayload event = new NotificationEventPayload(
                request.getUserId(),
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getReferenceId()
        );
        notificationService.publish(event);
        return ResponseEntity.accepted().body(Map.of("message", "Notification queued asynchronously"));
    }
}
