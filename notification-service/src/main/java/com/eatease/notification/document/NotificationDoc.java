package com.eatease.notification.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
public class NotificationDoc {

    @Id
    private String id;
    private Long userId;
    private String type;
    private String title;
    private String body;
    private String referenceId;
    private boolean read;
    private Instant createdAt;

    public NotificationDoc() {
    }

    public static NotificationDoc create(
            Long userId,
            String type,
            String title,
            String body,
            String referenceId) {

        NotificationDoc n = new NotificationDoc();
        n.userId = userId;
        n.type = type;
        n.title = title;
        n.body = body;
        n.referenceId = referenceId;
        n.read = false;
        n.createdAt = Instant.now();
        return n;
    }

    // getters & setters
    public String getId() { return id; }
    public Long getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getReferenceId() { return referenceId; }
    public boolean isRead() { return read; }
    public Instant getCreatedAt() { return createdAt; }
}
