package com.example.ubre.ui.dtos;

public class ChatDto {
    private Long id;
    private UserDto user;
    private Boolean hasUnreadMessages;

    public ChatDto() {}

    public ChatDto(Long id, UserDto user, Boolean hasUnreadMessages) {
        this.id = id;
        this.user = user;
        this.hasUnreadMessages = hasUnreadMessages;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public Boolean getHasUnreadMessages() { return hasUnreadMessages; }
    public void setHasUnreadMessages(Boolean hasUnreadMessages) { this.hasUnreadMessages = hasUnreadMessages; }
}
