package com.ubre.backend.controller;

import com.ubre.backend.dto.vehicle.VehicleDTO;
import com.ubre.backend.dto.vehicle.CreateVehicleDTO;
import com.ubre.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> createVehicle(
            @RequestParam Long driverId,
            @RequestBody CreateVehicleDTO createVehicleDTO) {
        try {
            VehicleDTO vehicle = vehicleService.createVehicle(driverId, createVehicleDTO);
            return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        try {
            VehicleDTO vehicle = vehicleService.getVehicleById(id);
            return new ResponseEntity<>(vehicle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> getVehicleByDriver(@PathVariable Long driverId) {
        try {
            VehicleDTO vehicle = vehicleService.getVehicleByDriver(driverId);
            return new ResponseEntity<>(vehicle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable Long id,
            @RequestBody CreateVehicleDTO updateVehicleDTO) {
        try {
            VehicleDTO vehicle = vehicleService.updateVehicle(id, updateVehicleDTO);
            return new ResponseEntity<>(vehicle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
