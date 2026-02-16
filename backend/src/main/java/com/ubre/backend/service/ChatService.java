package com.ubre.backend.service;

import com.ubre.backend.dto.ChatDto;
import com.ubre.backend.dto.ChatMessageDto;

import java.util.List;

public interface ChatService {
    public ChatDto findChatById(Long id);
    public List<ChatDto> findAllChats();
    public List<ChatMessageDto> findAllChatMessages(Long chatId);
    public void sendChatMessage(ChatMessageDto message);
    public Long getUnreadCount();
    public void markAsRead(Long messageId);
}
