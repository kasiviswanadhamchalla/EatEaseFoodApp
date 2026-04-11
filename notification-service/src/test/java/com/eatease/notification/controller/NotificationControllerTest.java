package com.eatease.notification.controller;

import com.eatease.notification.dto.NotificationPublishRequest;
import com.eatease.notification.dto.NotificationResponse;
import com.eatease.notification.event.NotificationEventPayload;
import com.eatease.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController controller;

    @Test
    void getMyNotificationsShouldReturnList() {
        List<NotificationResponse> expected = List.of(new NotificationResponse());
        when(notificationService.getByUserId(10L, 0, 20)).thenReturn(expected);

        ResponseEntity<List<NotificationResponse>> response = controller.getMyNotifications(10L, 0, 20);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getUnreadCountShouldReturnCount() {
        when(notificationService.getUnreadCount(10L)).thenReturn(5L);

        ResponseEntity<Map<String, Long>> response = controller.getUnreadCount(10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(5L, response.getBody().get("count"));
    }

    @Test
    void publishAsyncShouldPublishAndReturnAccepted() {
        NotificationPublishRequest request = new NotificationPublishRequest();
        request.setUserId(10L);
        request.setType("ORDER_UPDATE");
        request.setTitle("Order Placed");

        ResponseEntity<Map<String, String>> response = controller.publishAsync(request);

        assertEquals(202, response.getStatusCode().value());
        verify(notificationService).publish(any(NotificationEventPayload.class));
    }
}
