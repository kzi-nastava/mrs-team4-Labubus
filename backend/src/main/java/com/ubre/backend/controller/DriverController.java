package com.ubre.backend.controller;

import com.ubre.backend.dto.DriverRegistrationDto;
import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> createDriver(@RequestBody DriverRegistrationDto driverRegistrationDto) {
        UserDto createdDriver = driverService.createDriver(driverRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDriver);
    }

    // send notification to a driver about a new ride request
    @PostMapping(
            value = "/{id}/notify-ride-request", // id represents driver id
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDto> noitifyDriver(
            @PathVariable Long id,
            @RequestBody Long rideId
    ) {
        RideDto rideDto = driverService.notifyDriver(id, rideId);
        return ResponseEntity.status(HttpStatus.OK).body(rideDto);
    }

    // activate driver account (in request body, there is token, email and newPassword)
    @PostMapping(
            value = "/activate",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> activateDriverAccount(@RequestBody Map<String, String> activationData) {
        String token = activationData.get("token");
        String email = activationData.get("email");
        String newPassword = activationData.get("newPassword");
        driverService.activateDriverAccount(token, email, newPassword);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Collection<UserDto>> getAllDrivers() {
//        Collection<UserDto> drivers = driverService.getAllDrivers();
//        return new ResponseEntity<>(drivers, HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Collection<UserDto>> getAvailableDrivers() {
//        Collection<UserDto> drivers = driverService.getAvailableDrivers();
//        return new ResponseEntity<>(drivers, HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<UserDto> getDriverById(@PathVariable Long id) {
//        try {
//            UserDto driver = driverService.getDriverById(id);
//            return new ResponseEntity<>(driver, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping(value = "/{id}/toggle-availability")
//    public ResponseEntity<Void> toggleAvailability(@PathVariable Long id) {
//        try {
//            driverService.toggleAvailability(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping(value = "/{id}/approve-changes")
//    public ResponseEntity<Void> approveProfileChanges(@PathVariable Long id) {
//        try {
//            driverService.approveProfileChanges(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping(value = "/{id}/reject-changes")
//    public ResponseEntity<Void> rejectProfileChanges(@PathVariable Long id) {
//        try {
//            driverService.rejectProfileChanges(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
}
