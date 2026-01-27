package com.ubre.backend.service;

import com.ubre.backend.dto.*;

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
    RideDto endRide(Long rideId);
    void cancelRide(Long rideId, String reason);
    void stopRideInProgress(Long rideId);
    double estimateRidePrice(RideDto rideDto);
    List<RideDto> getRidesBetween(LocalDateTime start, LocalDateTime end);
    List<RideCardDto> getFavoriteRides(Long userId, Integer skip, Integer count, RideQueryDto queryDto);
    void addRideToFavorites(Long userId, Long rideId);
    void removeRideFromFavorites(Long userId, Long rideId);
    List<UserDto> getAvailableDrivers(RideDto rideDto);
    RideDto scheduleRide(Long userId, RideDto rideDto);

    List<RideCardDto> getRideHistory(Integer skip, Integer count, RideQueryDto query);
    List<RideCardDto> getMyRideHistory(Long userId, Integer skip, Integer count, RideQueryDto query);
    List<RideCardDto> getScheduledRides(Long driverId, Integer skip, Integer count, RideQueryDto query);
    RideDto getCurrentRide();
    void trackRide(Long id);

    RideDto orderRide(RideOrderDto rideDto);
}
