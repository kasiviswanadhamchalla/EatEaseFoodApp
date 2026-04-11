package com.eatease.notification.service;

import com.eatease.notification.document.NotificationDoc;
import com.eatease.notification.dto.NotificationResponse;
import com.eatease.notification.event.NotificationEventPayload;
import com.eatease.notification.producer.NotificationEventProducer;
import com.eatease.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    private DummyNotificationEventProducer notificationEventProducer;

    private NotificationServiceImpl notificationService;

    static class DummyNotificationEventProducer extends NotificationEventProducer {
        List<NotificationEventPayload> publishedPayloads = new ArrayList<>();
        public DummyNotificationEventProducer() { super(null); }
        @Override
        public void publish(NotificationEventPayload payload) {
            publishedPayloads.add(payload);
        }
    }

    @BeforeEach
    void setUp() {
        notificationEventProducer = new DummyNotificationEventProducer();
        notificationService = new NotificationServiceImpl(repository, notificationEventProducer);
    }

    @Test
    void getByUserIdShouldReturnMappedList() {
        NotificationDoc doc = NotificationDoc.create(5L, "TYPE", "Test", "Body", "Ref");
        org.springframework.test.util.ReflectionTestUtils.setField(doc, "id", "1");
        
        when(repository.findByUserIdOrderByCreatedAtDesc(eq(5L), any(PageRequest.class)))
                .thenReturn(List.of(doc));

        List<NotificationResponse> response = notificationService.getByUserId(5L, 0, 10);

        assertEquals(1, response.size());
        assertEquals("1", response.get(0).getId());
        assertEquals("Test", response.get(0).getTitle());
    }

    @Test
    void getUnreadCountShouldReturnCount() {
        when(repository.countByUserIdAndReadFalse(5L)).thenReturn(8L);

        long count = notificationService.getUnreadCount(5L);

        assertEquals(8L, count);
    }

    @Test
    void publishShouldInvokeProducer() {
        NotificationEventPayload event = new NotificationEventPayload(5L, "TEST", "Title", "Body", "Ref1");

        notificationService.publish(event);

        assertEquals(1, notificationEventProducer.publishedPayloads.size());
        assertEquals(5L, notificationEventProducer.publishedPayloads.get(0).getUserId());
    }
}
