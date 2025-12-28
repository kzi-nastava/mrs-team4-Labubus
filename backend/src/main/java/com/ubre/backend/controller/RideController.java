package com.ubre.backend.controller;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ride")
@CrossOrigin(origins = "*")
public class RideController {

    @Autowired
    private RideService rideService;

    // start a ride
    @PostMapping(value = "/{id}/start",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDto> startRide(@PathVariable Long id) {
        RideDto ride = rideService.startRide(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
        // verovatno ćemo morati da pošaljemo mnogo više podataka nazad sem riddto
    }

    // dobijanje omiljenih voznji korisnika
    @GetMapping(
            value = "/{userId}/favorites",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<RideDto>> getFavoriteRides(@PathVariable Long userId) {
        List<RideDto> favoriteRides = rideService.getFavoriteRides(userId);
        return ResponseEntity.status(HttpStatus.OK).body(favoriteRides);
    }

    // dodaj vožnju u omiljene (samo manje više promenimo flag da je favorite)
    @PutMapping(
            value = "/{userId}/favorites/{rideId}"
    )
    public ResponseEntity<Void> addRideToFavorites(
            @PathVariable Long userId,
            @PathVariable Long rideId) {
        rideService.addRideToFavorites(userId, rideId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // ukloni vožnju iz omiljenih
    @DeleteMapping(
            value = "/{userId}/favorites/{rideId}"
    )
    public ResponseEntity<Void> removeRideFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long rideId) {
        rideService.removeRideFromFavorites(userId, rideId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // kreiranje voznje
    @PostMapping(
            value = "/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDto> createRide(
            @PathVariable Long userId,
            @RequestBody RideDto rideDto) {
        RideDto createdRide = rideService.createRide(userId, rideDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRide);
    }

    // check wether there are drivers available for a ride
    @GetMapping(
            value = "/drivers-available",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<UserDto>> getAvailableDrivres(@RequestBody RideDto rideDto) {
        List<UserDto> availableDrivers = rideService.getAvailableDrivers(rideDto);
        return ResponseEntity.status(HttpStatus.OK).body(availableDrivers);
    }

    // schedule a ride for later
    @PostMapping(
            value = "/{userId}/schedule",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDto> scheduleRide(
            @PathVariable Long userId,
            @RequestBody RideDto rideDto) {
        RideDto scheduledRide = rideService.scheduleRide(userId, rideDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduledRide);
    }


//    @Autowired
//    private RideService rideService;
//
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RideDTO> createRide(
//            @RequestHeader("User-Id") Long userId,
//            @RequestBody CreateRideDTO createRideDTO) {
//        try {
//            RideDTO ride = rideService.createRide(userId, createRideDTO);
//            return new ResponseEntity<>(ride, HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RideDTO> getRideById(@PathVariable Long id) {
//        try {
//            RideDTO ride = rideService.getRideById(id);
//            return new ResponseEntity<>(ride, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<RideDTO>> getUserRides(@PathVariable Long userId) {
//        try {
//            List<RideDTO> rides = rideService.getUserRides(userId);
//            return new ResponseEntity<>(rides, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<RideDTO>> getDriverRides(@PathVariable Long driverId) {
//        try {
//            List<RideDTO> rides = rideService.getDriverRides(driverId);
//            return new ResponseEntity<>(rides, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping(value = "/{id}/start")
//    public ResponseEntity<Void> startRide(@PathVariable Long id) {
//        try {
//            rideService.startRide(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PutMapping(value = "/{id}/end")
//    public ResponseEntity<Void> endRide(@PathVariable Long id) {
//        try {
//            rideService.endRide(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<Void> cancelRide(
//            @PathVariable Long id,
//            @RequestParam(required = false) String reason) {
//        try {
//            rideService.cancelRide(id, reason);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PutMapping(value = "/{id}/accept")
//    public ResponseEntity<Void> acceptRide(
//            @PathVariable Long id,
//            @RequestParam Long driverId) {
//        try {
//            rideService.acceptRide(id, driverId);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PutMapping(value = "/{id}/reject")
//    public ResponseEntity<Void> rejectRide(
//            @PathVariable Long id,
//            @RequestParam String reason) {
//        try {
//            rideService.rejectRide(id, reason);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PostMapping(value = "/estimate", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Double> estimatePrice(@RequestBody CreateRideDTO createRideDTO) {
//        try {
//            double price = rideService.estimateRidePrice(createRideDTO);
//            return new ResponseEntity<>(price, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
}
