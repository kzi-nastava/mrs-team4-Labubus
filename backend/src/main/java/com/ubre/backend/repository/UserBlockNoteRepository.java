package com.ubre.backend.repository;

import com.ubre.backend.model.UserBlockNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlockNoteRepository extends JpaRepository<UserBlockNote, Long> {
    void deleteByUserId(Long userId);
    UserBlockNote findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
