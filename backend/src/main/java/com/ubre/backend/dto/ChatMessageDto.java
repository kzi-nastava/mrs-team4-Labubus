package com.ubre.backend.dto;

import com.ubre.backend.model.ChatMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    @NotNull(message = "Message can not have empty text")
    private String text;
    private LocalDateTime sentAt;
    private Boolean isRead = false;
    private Long chatId;
    private UserDto sender;

    public ChatMessageDto(ChatMessage model) {
        this.id = model.getId();
        this.text = model.getText();
        this.sentAt = model.getSentAt();
        this.isRead = model.isRead();
        this.chatId = model.getChat().getId();
        this.sender = new UserDto(model.getSender());
    }
}
