package com.ubre.backend.repository;

import com.ubre.backend.model.UserStatusRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRecordRepository extends JpaRepository<UserStatusRecord, Long> {
}