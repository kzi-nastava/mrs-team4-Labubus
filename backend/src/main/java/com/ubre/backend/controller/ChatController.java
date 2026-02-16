package com.ubre.backend.controller;

import com.ubre.backend.dto.ChatDto;
import com.ubre.backend.dto.ChatMessageDto;
import com.ubre.backend.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping(value = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid ChatMessageDto message) { // this represents ride id
        chatService.sendChatMessage(message);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChatDto> getChat(@PathVariable Long id) { // this represents ride id
        ChatDto chat = chatService.findChatById(id);
        return ResponseEntity.status(HttpStatus.OK).body(chat);
    }

    @GetMapping(value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatDto>> getAllChats() { // this represents ride id
        List<ChatDto> chat = chatService.findAllChats();
        return ResponseEntity.status(HttpStatus.OK).body(chat);
    }

    @GetMapping(value = "/{id}/messages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(@PathVariable Long id) { // this represents ride id
        List<ChatMessageDto> messages = chatService.findAllChatMessages(id);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @GetMapping(value = "messages/{messageId}/mark",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> getUnreadCount(@PathVariable Long messageId) { // this represents ride id
        chatService.markAsRead(messageId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping(value = "/unread",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Long> getUnreadCount() { // this represents ride id
        Long unreadCount = chatService.getUnreadCount();
        return ResponseEntity.status(HttpStatus.OK).body(unreadCount);
    }
}
