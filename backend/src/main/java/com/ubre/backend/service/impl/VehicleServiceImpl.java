package com.ubre.backend.service.impl;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Vehicle;
import com.ubre.backend.model.Waypoint;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.VehicleRepository;
import com.ubre.backend.repository.WaypointRepository;
import com.ubre.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private WaypointRepository waypointRepository;
    @Autowired
    private DriverRepository driverRepository;

    @Override
    public VehicleDto createVehicle(VehicleDto createVehicleDto, Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");

        createVehicleDto.setId(null);
        Vehicle newVehicle = new Vehicle(createVehicleDto);
        newVehicle.setDriver(driver.get());
        newVehicle = vehicleRepository.save(newVehicle);
        return new VehicleDto(newVehicle);
    }

    @Override
    public VehicleDto getVehicleById(Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");

        return new VehicleDto(vehicle.get());
    }

    @Override
    public VehicleDto getVehicleByDriver(Long driverId) {
        Driver driver = new Driver();
        driver.setId(driverId);
        Optional<Vehicle> vehicle = vehicleRepository.findByDriver(driver);
        if (vehicle.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");

        return new VehicleDto(vehicle.get());
    }

    @Override
    public VehicleDto updateVehicle(Long id, VehicleDto updateVehicleDto) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");

        Vehicle updatedVehicle = new Vehicle(updateVehicleDto);
        updatedVehicle.setId(id);
        return new VehicleDto(vehicleRepository.save(updatedVehicle));
    }

    @Override
    public VehicleDto deleteVehicle(Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");

        vehicleRepository.delete(vehicle.get());
        return new VehicleDto(vehicle.get());
    }

    @Override
    public Collection<VehicleIndicatorDto> getVehicleIndicators() {
        List<VehicleIndicatorDto> indicators = new ArrayList<>();
        List<Driver> activeDrivers = driverRepository.findActiveDrivers();
        for (Driver driver : activeDrivers) {
            if (driver.getVehicle() == null)
                continue;

            Optional<Waypoint> w = waypointRepository.findByVehicleId(driver.getVehicle().getId());
            if (w.isPresent())
                // TODO: Get the actual panic status from ride if the driver status is ON_RIDE
                indicators.add(new VehicleIndicatorDto(driver.getId(), new WaypointDto(w.get()), driver.getStatus(), false));
        }
        return indicators;
    }

    @Override
    public VehicleIndicatorDto getVehicleIndicator(Long id) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);

        if (vehicleOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");

        Vehicle vehicle = vehicleOptional.get();
        if (vehicle.getLocation() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not set");
        // TODO: Get the actual panic status from ride if the driver status is ON_RIDE
        return new VehicleIndicatorDto(vehicle.getDriver().getId(), new WaypointDto(vehicle.getLocation()), vehicle.getDriver().getStatus(), false);
    }

    @Override
    public VehicleIndicatorDto setVehicleIndicator(Long id, WaypointDto waypointDto) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);

        if (vehicleOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");

        Vehicle vehicle = vehicleOptional.get();
        Waypoint location = waypointRepository.save(new Waypoint(waypointDto));
        vehicle.setLocation(location);
        vehicle = vehicleRepository.save(vehicle);
        // TODO: Get the actual panic status from ride if the driver status is ON_RIDE
        return new VehicleIndicatorDto(vehicle.getDriver().getId(), new WaypointDto(vehicle.getLocation()), vehicle.getDriver().getStatus(), false);
    }
}
