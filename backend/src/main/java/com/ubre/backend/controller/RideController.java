package com.ubre.backend.controller;

import com.ubre.backend.dto.*;
import com.ubre.backend.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<List<RideCardDto>> getFavoriteRides(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer count,
            @ModelAttribute RideQueryDto query) {
        List<RideCardDto> favoriteRides = rideService.getFavoriteRides(userId, skip, count, query);
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

    @GetMapping(
            value = "/history/{userId}"
    )
    public ResponseEntity<List<RideCardDto>> getRideHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer count,
            @ModelAttribute RideQueryDto query) {
        List<RideCardDto> createdRide = rideService.getRideHistory(userId, skip, count, query);
        return ResponseEntity.status(HttpStatus.OK).body(createdRide);
    }

    @GetMapping(
            value = "/scheduled/{driverId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<RideCardDto>> getScheduledRides(
            @PathVariable Long driverId,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer count,
            @ModelAttribute RideQueryDto query) {
        List<RideCardDto> createdRide = rideService.getScheduledRides(driverId, skip, count, query);
        return ResponseEntity.status(HttpStatus.OK).body(createdRide);
    }

    @PostMapping(
            value = "/{id}/track",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> getScheduledRides(@PathVariable Long id) {
        rideService.trackRide(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // guest or registered user can estimate ride details based on provided waypoints and options
    @PostMapping(
            value = "/estimate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideEstimationsDto> estimateRide(@RequestBody RideDto rideDto) {
        double estimatedPrice = rideService.estimateRidePrice(rideDto);
        RideEstimationsDto estimationsDto = new RideEstimationsDto(
                new ArrayList<>(List.of(rideDto.getWaypoints())),
                estimatedPrice,
                15
        );
        return ResponseEntity.status(HttpStatus.OK).body(estimationsDto);
    }

    // cancel an already scheduled ride with optional reason
    @PutMapping(
            value = "/{id}/cancel",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> cancelRide(@PathVariable Long id, @RequestBody CancellationDto cancellationDto) {
        rideService.cancelRide(id, cancellationDto.getReason());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // stop a ride that is currently in progress
    @PutMapping(
            value = "/{id}/stop"
    )
    public ResponseEntity<Void> stopRideInProgress(@PathVariable Long id) {
        rideService.stopRideInProgress(id);
        return ResponseEntity.status(HttpStatus.OK).build();
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
    @PutMapping(value = "/{id}/end")
    public ResponseEntity<RideDto> endRide(
            @PathVariable Long id
    ) {
        RideDto ride = rideService.endRide(id);
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }
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
