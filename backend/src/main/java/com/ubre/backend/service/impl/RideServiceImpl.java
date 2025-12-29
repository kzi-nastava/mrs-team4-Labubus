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
        // postavljanje id-ja vožnje, može se koristiti neki generator id-jeva ili inkrementalni brojač
        // provera da li je korisnik već na nekoj vožnji, onda ne može
        UserDto foundUser = null;
        if (foundUser.getStatus() == UserStatus.ON_RIDE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already on a ride");
        }
        rideDto.setId((long) (rides.size() + 1));
        rides.add(rideDto);
        return rideDto; // i druge informacije po potrebi
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
    public RideDto startRide(Long rideId) {
        // prolazimo kroz vožnje (koje su inicijalno modeli i onda startujemo vožnju
        boolean found = false;
        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");
        }
        boolean accepted = false;
        if (!accepted) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride not accepted");
        }
        RideDto startedRide = new RideDto();

        return startedRide;
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
        // prolazimo kroz listu vožnji koje pripadaju kosirniku i uzimamo samo one koju su favorite boolean
        List<RideDto> temp = new ArrayList<>();
        for (RideDto ride : rides) {
            if (true) { // ovde ide provera da li je ride omiljena i da li je do ursera tačno
                temp.add(ride);
            }
        }
        return temp;
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
    public List<UserDto> getAvailableDrivers(RideDto rideDto) {
        // do a scan by availabilty and a suitable vehicle
        // if ride does not exist
        boolean rideExists = true;
        if (!rideExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");
        }

        boolean driversAvailable = false;
        if (!driversAvailable) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available drivers found for the ride");
        }

        boolean allBusy = false;
        if (allBusy) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All drivers are currently busy");
        }
        return List.of();
    }

    @Override
    public RideDto scheduleRide(Long userId, RideDto rideDto) {
        // provera da li korisnik već ima zakazanu vožnju u to vreme
        boolean hasScheduledRide = false;
        if (hasScheduledRide) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a scheduled ride at this time");
        }
        rideDto.setId((long) (rides.size() + 1));
        rides.add(rideDto);
        return rideDto;
    }
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
