package com.ubre.backend.repository;

import com.ubre.backend.model.Driver;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByActivationToken(String token);
    Optional<Driver> findByVehicle(Vehicle vehicle);

    @Query("SELECT d FROM Driver d WHERE d.status <> 'INACTIVE'")
    List<Driver> findActiveDrivers();
//    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.currentStatus = :status AND d.activeHoursLast24h < 8")
//    List<Driver> findAvailableDrivers(UserStatus status);
    
//    List<Driver> findByIsAvailableTrue();

    List<Driver> findByStatus(UserStatus status);
    Boolean existsByStatus(UserStatus status);

    // check if there is single driver taht has status on ride or active
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Driver d WHERE d.status IN ('ON_RIDE', 'ACTIVE')")
    Boolean existsDriverWithActiveStatus();

    // check if every single driver is on ride and has a ride that is pending at the same time (driver has no attribute ride, ride has attribute driver)
    @Query("SELECT CASE WHEN COUNT(d) = (SELECT COUNT(d2) FROM Driver d2) THEN true ELSE false END FROM Driver d WHERE d.status = 'ON_RIDE' AND EXISTS (SELECT r FROM Ride r WHERE r.driver = d AND r.status = 'PENDING')")
    Boolean areAllDriversOnRideWithPendingRides();


}
