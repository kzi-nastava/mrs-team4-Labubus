package com.ubre.backend.dto;

import com.ubre.backend.enums.NotificationType;

// For system notifications

public class NotificationDto {
    private int id;
    private int userId;
    private String title;
    private String message;
    private boolean read;
    private NotificationType type;

    public NotificationDto() {
    }
    public NotificationDto(int id, int userId, String title, String message, boolean read, NotificationType type) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.read = read;
        this.type = type;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
