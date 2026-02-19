package com.example.ubre.ui.dtos;

public class ChatMessageDto {
    private Long id;
    private String text;
    private String sentAt;
    private Boolean isRead;
    private Long chatId;
    private UserDto sender;

    public ChatMessageDto() {}

    public ChatMessageDto(Long id, String text, String sentAt, Boolean isRead, Long chatId, UserDto sender) {
        this.id = id;
        this.text = text;
        this.sentAt = sentAt;
        this.isRead = isRead;
        this.chatId = chatId;
        this.sender = sender;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public UserDto getSender() { return sender; }
    public void setSender(UserDto sender) { this.sender = sender; }
}
