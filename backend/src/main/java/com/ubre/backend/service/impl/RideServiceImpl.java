package com.ubre.backend.service.impl;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RideServiceImpl implements RideService {

    // Mock data for rides
    List<RideDto> rides = new ArrayList<RideDto>();

    @Override
    public RideDto createRide(Long userId, RideDto rideDto) {
        return null;
    }

    @Override
    public RideDto getRideById(Long id) {
        RideDto ride = rides.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        // neophodno je proveriti takodje da li je ride prihvaćen, onda može da se startuje
        // tada vraćamo kod 400
        return ride;
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
        // povučemo ride iz baze
        // todo: izvući bitne podatke iz ride i napraviti objekat
        // postaviti ride status na true
        // ako je error baci exception
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

    @Override
    public List<RideDto> getFavoriteRides(Long userId) {
        // pretaržujemo vožnje po atributu da li su omiljene ili nisu
        return List.of();
    }

    @Override
    public void addRideToFavorites(Long userId, Long rideId) {
        // pronaći vožnju i postaviti atribut omiljene na true
        boolean found = false;
        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");
        }
    }

    @Override
    public void removeRideFromFavorites(Long userId, Long rideId) {
        // pronaći vožnju i postaviti atribut omiljene na false
        boolean found = false;
        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");
        }
    }
}
