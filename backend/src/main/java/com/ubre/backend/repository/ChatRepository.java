package com.ubre.backend.repository;

import com.ubre.backend.model.Chat;
import com.ubre.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByUser(User user);
}
