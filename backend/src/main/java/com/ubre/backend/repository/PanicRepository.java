package com.ubre.backend.repository;

import com.ubre.backend.model.PanicNotification;
import com.ubre.backend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanicRepository extends JpaRepository<PanicNotification, Long> {
    List<PanicNotification> findAllByOrderByTimestampDesc();
}
