package com.ubre.backend.dto;

import com.ubre.backend.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Normalized;

// For system notifications

@Getter
@Setter
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private Boolean read;
    private NotificationType type;

    public NotificationDto(Long id, Long userId, String title, String message, boolean read, NotificationType type) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.read = read;
        this.type = type;
    }
}
