package com.ubre.backend.repository;

import com.ubre.backend.model.Driver;
import com.ubre.backend.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByActivationToken(String token);
    
    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.currentStatus = :status AND d.activeHoursLast24h < 8")
    List<Driver> findAvailableDrivers(UserStatus status);
    
    List<Driver> findByIsAvailableTrue();
}
