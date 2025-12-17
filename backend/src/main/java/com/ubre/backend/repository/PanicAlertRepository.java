package com.ubre.backend.repository;

import com.ubre.backend.model.PanicAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PanicAlertRepository extends JpaRepository<PanicAlert, Long> {
    List<PanicAlert> findByIsResolvedFalseOrderByActivatedAtDesc();
    List<PanicAlert> findAllByOrderByActivatedAtDesc();
}
