package com.ubre.backend.service.impl;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.RideQueryDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
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
    public RideDto endRide(Long rideId) {
        UserDto driver = new UserDto(1L, Role.DRIVER, "", "driver@ubre.com", "Driver", "Driver", "1231234132", "Adress 123", UserStatus.ACTIVE);
        WaypointDto[] waypoints = new WaypointDto[] {
                new WaypointDto(1L, "Bulevar oslobodjenja", 48.83, 19.32),
                new WaypointDto(2L, "Trg mladenaca", 48.83, 19.32),
                new WaypointDto(3L, "Bulevar despota Stefana", 48.83, 19.32)
        };
        UserDto[] passengers = {
                new UserDto(2L, Role.REGISTERED_USER, "", "passenger1@ubre.com", "Passenger1", "Passenger1", "1231234132", "Adress 123", UserStatus.ACTIVE),
                new UserDto(3L, Role.REGISTERED_USER, "", "passenger2@ubre.com", "Passenger2", "Passenger2", "1231234132", "Adress 123", UserStatus.ACTIVE)
        };

        return new RideDto(1L, LocalDateTime.now(), LocalDateTime.now(), waypoints, driver, Arrays.stream(passengers).toList(), true, null, 12.34, 7.3);
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

    @Override
    public List<RideDto> getRideHistory(Long userId, Integer skip, Integer count, RideQueryDto query) {
        UserDto driver = new UserDto(1L, Role.DRIVER, "", "driver@ubre.com", "Driver", "Driver", "1231234132", "Adress 123", UserStatus.ACTIVE);
        WaypointDto[] waypoints = new WaypointDto[] {
                new WaypointDto(1L, "Bulevar oslobodjenja", 48.83, 19.32),
                new WaypointDto(2L, "Trg mladenaca", 48.83, 19.32),
                new WaypointDto(3L, "Bulevar despota Stefana", 48.83, 19.32)
        };
        UserDto[] passengers = {
                new UserDto(2L, Role.REGISTERED_USER, "", "passenger1@ubre.com", "Passenger1", "Passenger1", "1231234132", "Adress 123", UserStatus.ACTIVE),
                new UserDto(3L, Role.REGISTERED_USER, "", "passenger2@ubre.com", "Passenger2", "Passenger2", "1231234132", "Adress 123", UserStatus.ACTIVE)
        };

        return List.of(
                new RideDto(1L, LocalDateTime.now(), LocalDateTime.now(), waypoints, driver, Arrays.stream(passengers).toList(), true, null, 12.34, 7.3),
                new RideDto(2L, LocalDateTime.now(), LocalDateTime.now(), waypoints, driver, Arrays.stream(passengers).toList(), false, null, 20.14, 12.1)
        );
    }

    @Override
    public List<RideDto> getScheduledRides(Long driverId, Integer skip, Integer count, RideQueryDto query) {
        return getRideHistory(driverId, skip, count, query);
    }

    @Override
    public void trackRide(Long id) {

    }
}
