package com.ubre.backend.repository;

import com.ubre.backend.model.UserStatusRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatusRecordRepository extends JpaRepository<UserStatusRecord, Long> {
    // this method will be used to get the status history of a driver for a given time range, ordered by validFrom ascending
    List<UserStatusRecord> findByUserIdAndValidFromBetweenOrderByValidFromAsc(
            Long userId, LocalDateTime from, LocalDateTime to);

    // this method will be used to get the most recent status record for a driver that is valid at a given time, ordered by validFrom descending
    Optional<UserStatusRecord> findTopByUserIdAndValidFromLessThanOrderByValidFromDesc(
            Long userId, LocalDateTime from);
}