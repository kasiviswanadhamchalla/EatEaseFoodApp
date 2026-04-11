package com.eatease.notification.event;

public class NotificationEventPayload {
    private Long userId;
    private String type;
    private String title;
    private String body;
    private String referenceId;

    public NotificationEventPayload() {
    }

    public NotificationEventPayload(Long userId, String type, String title, String body, String referenceId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.referenceId = referenceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
