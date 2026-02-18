package com.ubre.backend.service.impl;

import com.ubre.backend.dto.ChatDto;
import com.ubre.backend.dto.ChatMessageDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.model.Chat;
import com.ubre.backend.model.ChatMessage;
import com.ubre.backend.model.User;
import com.ubre.backend.repository.ChatMessageRepository;
import com.ubre.backend.repository.ChatRepository;
import com.ubre.backend.service.ChatService;
import com.ubre.backend.websocket.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @Override
    public ChatDto findChatById(Long id) {
        return new ChatDto(checkChatAccessCredentials(id));
    }

    @Override
    public List<ChatDto> findAllChats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (jwtUser == null || jwtUser.getRole() != Role.ADMIN)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admins can access all chats");

        return chatRepository.findAll().stream().map(ChatDto::new).toList();
    }

    @Override
    public List<ChatMessageDto> findAllChatMessages(Long chatId) {
        Chat chat = checkChatAccessCredentials(chatId);
        return chatMessageRepository.findByChatOrderBySentAtDesc(chat).stream().map(ChatMessageDto::new).toList();
    }

    @Override
    public void sendChatMessage(ChatMessageDto message) {
        User jwtUser = getCurrentUser();

        Chat chat;
        if (jwtUser.getRole() == Role.ADMIN)
            chat = checkChatAccessCredentials(message.getChatId());
        else {
            Optional<Chat> myChat = chatRepository.findByUser(jwtUser);
            chat = myChat.orElseGet(() -> new Chat(jwtUser));
        }

        ChatMessage messageModel = new ChatMessage(message);
        messageModel.setChat(chat);
        messageModel.setSender(jwtUser);
        chat.getMessages().add(messageModel);
        chat = chatRepository.save(chat);

        ChatMessage sentMessage = chat.getMessages().get(chat.getMessages().size() - 1);
        webSocketNotificationService.sendChatMessage(chat.getUser().getId(), new ChatMessageDto(sentMessage));
    }

    @Override
    public Long getUnreadCount() {
        User jwtUser = getCurrentUser();

        if (jwtUser.getRole() == Role.ADMIN)
            return chatMessageRepository.countBySenderRoleNotAndIsReadFalse(Role.ADMIN);

        Optional<Chat> chat = chatRepository.findByUser(jwtUser);
        if (chat.isEmpty())
            return 0L;
        return chat.get().getMessages().stream().filter(message -> !message.isRead() && !message.getSender().getId().equals(jwtUser.getId())).count();
    }

    public void markAsRead(Long messageId) {
        User jwtUser = getCurrentUser();

        Optional<ChatMessage> message = chatMessageRepository.findById(messageId);
        if (message.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");

        if (jwtUser.getRole() != Role.ADMIN && !message.get().getChat().getUser().getId().equals(jwtUser.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this chat");

        message.get().setRead(true);
        chatMessageRepository.save(message.get());
    }

    public ChatDto findMyChat() {
        User jwtUser = getCurrentUser();

        Optional<Chat> chat = chatRepository.findByUser(jwtUser);
        return chat.map(ChatDto::new).orElse(null);
    }

    private Chat checkChatAccessCredentials(Long chatId) {
        User jwtUser = getCurrentUser();

        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found");

        if (jwtUser.getRole() != Role.ADMIN && !chat.get().getUser().getId().equals(jwtUser.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this chat");

        return chat.get();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (jwtUser == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unregistered users may not use the live support");

        return jwtUser;
    }
}
