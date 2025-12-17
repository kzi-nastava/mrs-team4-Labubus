package com.ubre.backend.service;

import com.ubre.backend.dto.ride.CreateRideDTO;
import com.ubre.backend.dto.ride.RideDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface RideService {
    RideDTO createRide(Long userId, CreateRideDTO createRideDTO);
    RideDTO getRideById(Long id);
    List<RideDTO> getUserRides(Long userId);
    List<RideDTO> getDriverRides(Long driverId);
    void acceptRide(Long rideId, Long driverId);
    void rejectRide(Long rideId, String reason);
    void startRide(Long rideId);
    void endRide(Long rideId);
    void cancelRide(Long rideId, String reason);
    void stopRideInProgress(Long rideId);
    double estimateRidePrice(CreateRideDTO createRideDTO);
    List<RideDTO> getRidesBetween(LocalDateTime start, LocalDateTime end);
}
