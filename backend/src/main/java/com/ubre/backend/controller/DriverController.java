package com.ubre.backend.controller;

import com.ubre.backend.dto.DriverRegistrationDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/driver")
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
