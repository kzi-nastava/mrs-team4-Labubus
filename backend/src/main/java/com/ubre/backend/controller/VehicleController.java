package com.ubre.backend.controller;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> createVehicle(
            @RequestParam Long driverId,
            @RequestBody VehicleDto createVehicleDto) {
        try {
            VehicleDto vehicle = vehicleService.createVehicle(driverId, createVehicleDto);
            return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        try {
            VehicleDto vehicle = vehicleService.getVehicleById(id);
            return new ResponseEntity<>(vehicle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> getVehicleByDriver(@PathVariable Long driverId) {
        try {
            VehicleDto vehicle = vehicleService.getVehicleByDriver(driverId);
            return new ResponseEntity<>(vehicle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleDto updateVehicleDto) {
        try {
            VehicleDto vehicle = vehicleService.updateVehicle(id, updateVehicleDto);
            return new ResponseEntity<>(vehicle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<VehicleDto> deleteVehicle(@PathVariable Long id) {
        try {
            VehicleDto vehicle = vehicleService.deleteVehicle(id);
            return new ResponseEntity<VehicleDto>(vehicle, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<VehicleDto>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<VehicleIndicatorDto>> getVehicleIndicators() {
        try {
            Collection<VehicleIndicatorDto> locations = vehicleService.getVehicleIndicators();
            return new ResponseEntity<>(locations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleIndicatorDto> getVehicleIndicator(@PathVariable Long id) {
        try {
            VehicleIndicatorDto location = vehicleService.getVehicleIndicator(id);
            return new ResponseEntity<>(location, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
