package com.ubre.backend.repository;

import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.model.Waypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Long> {
    @Query("SELECT w FROM Vehicle v JOIN v.location w WHERE v.id = :vehicleId")
    Optional<Waypoint> findByVehicleId(@Param("vehicleId") Long vehicleId);
}
