package com.ubre.backend.repository;

import com.ubre.backend.model.Ride;
import com.ubre.backend.model.Driver;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByCreatorOrderByStartTimeDesc(User creator);
    List<Ride> findByDriverOrderByStartTimeDesc(Driver driver);
//    List<Ride> findByRideStatus(RideStatus status);
    
//    @Query("SELECT r FROM Ride r WHERE r.creator = :user OR :user MEMBER OF r.passengers ORDER BY r.startTime DESC")
//    List<Ride> findByUserAsCreatorOrPassenger(@Param("user") User user);
//
//    @Query("SELECT r FROM Ride r WHERE r.startTime BETWEEN :start AND :end")
//    List<Ride> findRidesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
//
//    @Query("SELECT r FROM Ride r WHERE r.driver = :driver AND r.startTime BETWEEN :start AND :end")
//    List<Ride> findDriverRidesBetween(@Param("driver") Driver driver, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
