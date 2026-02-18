package com.ubre.backend.repository;

import com.ubre.backend.enums.Role;
import com.ubre.backend.model.Chat;
import com.ubre.backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatOrderBySentAtDesc(Chat chat);
    Long countBySenderRoleNotAndIsReadFalse(Role role);
}
