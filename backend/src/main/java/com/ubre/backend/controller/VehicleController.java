package com.ubre.backend.controller;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping(value = "/driver/{driverId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> createVehicle(
            @RequestBody VehicleDto createVehicleDto,
            @PathVariable Long driverId) {
        VehicleDto vehicle = vehicleService.createVehicle(createVehicleDto, driverId);
        return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
    }

    // get a drivers vehicle by veihicle id
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        VehicleDto vehicle = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> getVehicleByDriver(@PathVariable Long driverId) {
        VehicleDto vehicle = vehicleService.getVehicleByDriver(driverId);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleDto updateVehicleDto) {
        VehicleDto vehicle = vehicleService.updateVehicle(id, updateVehicleDto);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> deleteVehicle(@PathVariable Long id) {
        VehicleDto vehicle = vehicleService.deleteVehicle(id);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @GetMapping(value = "/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VehicleIndicatorDto>> getVehicleIndicators() {
        List<VehicleIndicatorDto> locations = vehicleService.getVehicleIndicators();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleIndicatorDto> getVehicleIndicator(@PathVariable Long id) {
        VehicleIndicatorDto location = vehicleService.getVehicleIndicator(id);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<VehicleIndicatorDto> setVehicleIndicator(@PathVariable Long id, @RequestBody WaypointDto location) {
        VehicleIndicatorDto setLocation = vehicleService.setVehicleIndicator(id, location);
        return new ResponseEntity<>(setLocation, HttpStatus.OK);
    }
}
