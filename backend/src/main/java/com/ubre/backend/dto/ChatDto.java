package com.ubre.backend.dto;

import com.ubre.backend.model.Chat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long id;
    @NotNull(message = "Chat must be dedicated to 1 user")
    private UserDto user;
    @NotNull(message = "Chat must have information about read messages")
    private Boolean hasUnreadMessages;

    public ChatDto(Chat model) {
        this.id = model.getId();
        this.user = new UserDto(model.getUser());
        this.hasUnreadMessages = model.getMessages().stream().anyMatch(message -> !message.isRead());
    }
}
