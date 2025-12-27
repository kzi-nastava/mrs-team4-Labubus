package com.ubre.backend.dto;

import com.ubre.backend.enums.NotificationType;

// For system notifications

public class NotificationDto {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private Boolean read;
    private NotificationType type;

    public NotificationDto() {
    }
    public NotificationDto(Long id, Long userId, String title, String message, boolean read, NotificationType type) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.read = read;
        this.type = type;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
