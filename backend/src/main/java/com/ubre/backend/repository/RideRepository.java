package com.ubre.backend.repository;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.model.Ride;
import com.ubre.backend.model.Driver;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByStatusIn(Collection<RideStatus> statuses, Pageable pageable);
    List<Ride> findByCreatorAndStatusIn(User creator, Collection<RideStatus> statuses, Pageable pageable);
    List<Ride> findByDriverAndStatusIn(Driver driver, Collection<RideStatus> statuses, Pageable pageable);
    List<Ride> findByCreatorAndStatusInAndStartTimeBetween(User creator, Collection<RideStatus> statuses, LocalDateTime startStartTime, LocalDateTime endStartTime, Pageable pageable);
    List<Ride> findByDriverAndStatusInAndStartTimeBetween(Driver driver, Collection<RideStatus> statuses, LocalDateTime startStartTime, LocalDateTime endStartTime, Pageable pageable);
    List<Ride> findByStatusInAndStartTimeBetween(Collection<RideStatus> statuses, LocalDateTime startStartTime, LocalDateTime endStartTime, Pageable pageable);
    List<Ride> findByCreatorAndFavoriteTrue(User creator, Pageable pageable);
    List<Ride> findByCreatorAndFavoriteTrueAndStartTimeBetween(User creator, LocalDateTime startStartTime, LocalDateTime endStartTime, Pageable pageable);
    Optional<Ride> findFirstByDriverAndStatusOrderByStartTimeDesc(Driver driver, RideStatus status);

//    List<Ride> findByRideStatus(RideStatus status);
    
//    @Query("SELECT r FROM Ride r WHERE r.creator = :user OR :user MEMBER OF r.passengers ORDER BY r.startTime DESC")
//    List<Ride> findByUserAsCreatorOrPassenger(@Param("user") User user);
//
//    @Query("SELECT r FROM Ride r WHERE r.startTime BETWEEN :start AND :end")
//    List<Ride> findRidesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
//
//    @Query("SELECT r FROM Ride r WHERE r.driver = :driver AND r.startTime BETWEEN :start AND :end")
//    List<Ride> findDriverRidesBetween(@Param("driver") Driver driver, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // find rides by ride status
    List<Ride> findByStatus(RideStatus status);


    @Query("""
    select new com.ubre.backend.dto.RideDto(r)
    from Ride r
    where r.driver.id = :id
      and (r.status = 'PENDING' or r.status = 'IN_PROGRESS')
    """)
    Optional<RideDto> findDriverActiveRide(Long id);

    @Query("""
    select new com.ubre.backend.dto.RideDto(r)
    from Ride r
    where r.creator.id = :id
      and (r.status = 'PENDING' or r.status = 'IN_PROGRESS')
    """)
    Optional<RideDto> findUserActiveRide(Long id);

}
