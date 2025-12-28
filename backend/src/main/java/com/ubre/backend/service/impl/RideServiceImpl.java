package com.ubre.backend.service.impl;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.service.RideService;

import java.time.LocalDateTime;
import java.util.List;

public class RideServiceImpl implements RideService {
    @Override
    public RideDto createRide(Long userId, RideDto rideDto) {
        return null;
    }

    @Override
    public RideDto getRideById(Long id) {
        return null;
    }

    @Override
    public List<RideDto> getUserRides(Long userId) {
        return List.of();
    }

    @Override
    public List<RideDto> getDriverRides(Long driverId) {
        return List.of();
    }

    @Override
    public void acceptRide(Long rideId, Long driverId) {

    }

    @Override
    public void rejectRide(Long rideId, String reason) {

    }

    @Override
    public void startRide(Long rideId) {

    }

    @Override
    public void endRide(Long rideId) {

    }

    @Override
    public void cancelRide(Long rideId, String reason) {

    }

    @Override
    public void stopRideInProgress(Long rideId) {

    }

    @Override
    public double estimateRidePrice(RideDto rideDto) {
        return 0;
    }

    @Override
    public List<RideDto> getRidesBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }
}
