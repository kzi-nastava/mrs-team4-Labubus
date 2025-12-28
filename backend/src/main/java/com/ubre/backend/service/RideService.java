package com.ubre.backend.service;

import com.ubre.backend.dto.RideDto;
import java.time.LocalDateTime;
import java.util.List;

public interface RideService {
    RideDto createRide(Long userId, RideDto rideDto);
    RideDto getRideById(Long id);
    List<RideDto> getUserRides(Long userId);
    List<RideDto> getDriverRides(Long driverId);
    void acceptRide(Long rideId, Long driverId);
    void rejectRide(Long rideId, String reason);
    void startRide(Long rideId);
    void endRide(Long rideId);
    void cancelRide(Long rideId, String reason);
    void stopRideInProgress(Long rideId);
    double estimateRidePrice(RideDto rideDto);
    List<RideDto> getRidesBetween(LocalDateTime start, LocalDateTime end);
    List<RideDto> getFavoriteRides(Long userId);
    void addRideToFavorites(Long userId, Long rideId);
    void removeRideFromFavorites(Long userId, Long rideId);
}
