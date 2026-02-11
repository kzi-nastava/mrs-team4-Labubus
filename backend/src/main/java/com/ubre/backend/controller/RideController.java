package com.ubre.backend.controller;

import com.ubre.backend.dto.*;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.PanicNotification;
import com.ubre.backend.service.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "*")
public class RideController {

    @Autowired
    private RideService rideService;

    // just change status of a ride from pending to a in progress
    @PostMapping(value = "/{id}/start",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> startRide(@PathVariable Long id) { // this represents ride id
        rideService.startRide(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // dobijanje omiljenih voznji korisnika
    @PreAuthorize("#userId == @securityUtil.currentUserId()")
    @GetMapping(
            value = "/{userId}/favorites",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<RideCardDto>> getFavoriteRides(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer count,
            @Valid @ModelAttribute RideQueryDto query) {
        List<RideCardDto> favoriteRides = rideService.getFavoriteRides(userId, skip, count, query);
        return ResponseEntity.status(HttpStatus.OK).body(favoriteRides);
    }

    // dodaj vožnju u omiljene (samo manje više promenimo flag da je favorite)
    @PutMapping(
            value = "/{userId}/favorites/{rideId}"
    )
    @PreAuthorize("#userId == @securityUtil.currentUserId()")
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
    @PreAuthorize("#userId == @securityUtil.currentUserId()")
    public ResponseEntity<Void> removeRideFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long rideId) {
        rideService.removeRideFromFavorites(userId, rideId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // kreiranje voznje
//    @PostMapping(
//            value = "/{userId}",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<RideDto> createRide(
//            @PathVariable Long userId,
//            @RequestBody RideDto rideDto) {
//        RideDto createdRide = rideService.createRide(userId, rideDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdRide);
//    }

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
            @Valid @RequestBody RideDto rideDto) {
        RideDto scheduledRide = rideService.scheduleRide(userId, rideDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduledRide);
    }

    @GetMapping(
            value = "/history/{userId}"
    )
    @PreAuthorize("#userId == @securityUtil.currentUserId() || hasRole('ADMIN')")
    public ResponseEntity<List<RideCardDto>> getRideHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer count,
            @Valid @ModelAttribute RideQueryDto query) {
        List<RideCardDto> rides = rideService.getMyRideHistory(userId, skip, count, query);
        return ResponseEntity.status(HttpStatus.OK).body(rides);
    }

    @GetMapping(
            value = "/history"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RideCardDto>> getRideHistory(
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer count,
            @Valid @ModelAttribute RideQueryDto query) {
        List<RideCardDto> rides = rideService.getRideHistory(skip, count, query);
        return ResponseEntity.status(HttpStatus.OK).body(rides);
    }

    @GetMapping(
            value = "/scheduled/{driverId}"
    )
    @PreAuthorize("#driverId == @securityUtil.currentUserId() || hasRole('ADMIN')")
    public ResponseEntity<List<RideCardDto>> getScheduledRides(
            @PathVariable Long driverId,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer count,
            @Valid @ModelAttribute RideQueryDto query) {
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

    // cancel an already scheduled ride with optional reason
    @PutMapping( "/{rideId}/cancel/user")
    @PreAuthorize("hasRole('REGISTERED_USER')")
    public ResponseEntity<Void> cancelRide(@PathVariable Long rideId) {
        rideService.cancelRide(rideId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // stop a ride that is currently in progress
    @PutMapping(value = "/{id}/stop")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Double>  stopRideInProgress(@PathVariable Long id, @Valid @RequestBody WaypointDto waypoint) {
        Double price = rideService.stopRideInProgress(id, waypoint);
        return ResponseEntity.status(HttpStatus.OK).body(price);
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
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideDto> getRideById(@PathVariable Long id) {
        RideDto ride = rideService.getRideById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ride);
    }
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

    // TODO: implement price fetching from database later
    @PostMapping(value = "/price-estimate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Double> estimatePrice(@RequestBody Map<String, Double> options) {
        Double standard = 5.0; // base fare for standard vehicle (in dollars)
        Double van = 8.0; // base fare for van vehicle
        Double luxury = 20.0; // base fare for luxury vehicle
        Double perKm = 1.2; // per kilometer rate (in dollars)

        Double dist = options.get("distance"); // in meters
        Double vehicleType = options.get("vehicleType"); // 0 - standard, 1 - van, 2 - luxury

        // if there is no distance or vehicle type in the request, return bad request (if they are null for example)
        if (dist == null || vehicleType == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        double baseFare;
        if (vehicleType == 0) {
            baseFare = standard;
        } else if (vehicleType == 1) {
            baseFare = van;
        } else {
            baseFare = luxury;
        }
        double price = baseFare + (perKm * (dist / 1000));
        // rounding to 2 decimal places
        price = Math.round(price * 100.0) / 100.0;
        return ResponseEntity.status(HttpStatus.OK).body(price);
    }

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RideDto> getCurrentRide() {
        RideDto ride = rideService.getCurrentRide();
        return ResponseEntity.status(HttpStatus.OK).body(ride);
    }




    // order a ride endpoint, should be protected later by hasRole('USER') or similar, for now ignore
    // TODO: protect this endpoint later
    @PostMapping(
            value = "/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDto> orderRide(@Valid @RequestBody RideOrderDto rideOrder) {
        RideDto orederedRide = rideService.orderRide(rideOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(orederedRide);
    }

    @PutMapping("/{rideId}/cancel/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideDto> cancelRideByDriver(@PathVariable Long rideId, @Valid @RequestBody CancellationDto request) {
        RideDto cancelledRide = rideService.cancelRideByDriver(rideId, request.getReason());
        return ResponseEntity.status(HttpStatus.OK).body(cancelledRide);
    }

    @PostMapping("/{rideId}/panic")
    @PreAuthorize("hasAnyRole('DRIVER','REGISTERED_USER')")
    public ResponseEntity<?> activatePanic(@PathVariable Long rideId) {
        rideService.activatePanic(rideId);
        return ResponseEntity.ok(Map.of("message", "PANIC activated"));
    }

    @GetMapping("/panic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PanicNotification>> getPanics() {
        List<PanicNotification> panics = rideService.getPanics();
        return ResponseEntity.ok(panics);
    }
}
