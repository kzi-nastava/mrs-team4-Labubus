package com.ubre.backend.service.impl;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.NotificationType;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.VehicleRepository;
import com.ubre.backend.repository.WaypointRepository;
import com.ubre.backend.service.VehicleService;
import com.ubre.backend.websocket.VehicleLocationNotification;
import com.ubre.backend.websocket.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private WebSocketNotificationService notificationService;

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
    public List<VehicleIndicatorDto> getVehicleIndicators() {
        List<VehicleIndicatorDto> indicators = new ArrayList<>();
        List<Driver> activeDrivers = driverRepository.findActiveDrivers();
        for (Driver driver : activeDrivers) {
            if (driver.getVehicle() == null || driver.getVehicle().getLocation() == null)
                continue;

            Optional<Waypoint> w = waypointRepository.findById(driver.getVehicle().getLocation().getId());
            if (w.isPresent()) {
                if (driver.getStatus().equals(UserStatus.ON_RIDE)) {
                    Optional<Ride> ride = rideRepository.findFirstByDriverAndStatusOrderByStartTimeAsc(driver, RideStatus.IN_PROGRESS);
                    if (!ride.isEmpty()) {
                        indicators.add(new VehicleIndicatorDto(driver.getId(), new WaypointDto(w.get()), driver.getStatus(), ride.get().getPanic()));
                        continue;
                    }
                }
                indicators.add(new VehicleIndicatorDto(driver.getId(), new WaypointDto(w.get()), driver.getStatus(), false));
            }
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

        if (vehicle.getDriver().getStatus().equals(UserStatus.ON_RIDE)) {
            Optional<Ride> ride = rideRepository.findFirstByDriverAndStatusOrderByStartTimeAsc(vehicle.getDriver(), RideStatus.IN_PROGRESS);
            if (!ride.isEmpty())
                return new VehicleIndicatorDto(vehicle.getDriver().getId(), new WaypointDto(vehicle.getLocation()), vehicle.getDriver().getStatus(), ride.get().getPanic());
        }
        return new VehicleIndicatorDto(vehicle.getDriver().getId(), new WaypointDto(vehicle.getLocation()), vehicle.getDriver().getStatus(), false);
    }

    @Override
    public VehicleIndicatorDto setVehicleIndicator(Long id, WaypointDto waypointDto) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);

        if (vehicleOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (!jwtUser.getId().equals(vehicleOptional.get().getDriver().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update the location of your vehicle");

        Vehicle vehicle = vehicleOptional.get();
        waypointDto.setLabel(vehicle.getDriver().getName() + " " + vehicle.getDriver().getSurname() + " - " + vehicle.getModel());
        Waypoint newLocation = new Waypoint(waypointDto);
        if (vehicle.getLocation() != null)
            newLocation.setId(vehicle.getLocation().getId());
        waypointRepository.save(newLocation);

        Optional<Ride> ride = rideRepository.findFirstByDriverAndStatusOrderByStartTimeAsc(vehicle.getDriver(), RideStatus.IN_PROGRESS);
        if (!ride.isEmpty()) {
            for (Waypoint waypoint : ride.get().getWaypoints()) {
                if (waypoint.getVisited() == null || !waypoint.getVisited()) {
                    if (Math.abs(waypoint.getLongitude() - waypointDto.getLongitude()) < 0.00054 && Math.abs(waypoint.getLatitude() - waypointDto.getLatitude()) < 0.00030) {
                        waypoint.setVisited(true);
                        waypointRepository.save(waypoint);
                    }
                    break;
                }
            }
            return new VehicleIndicatorDto(vehicle.getDriver().getId(), new WaypointDto(vehicle.getLocation()), vehicle.getDriver().getStatus(), ride.get().getPanic());
        }
        return new VehicleIndicatorDto(vehicle.getDriver().getId(), new WaypointDto(vehicle.getLocation()), vehicle.getDriver().getStatus(), false);
    }

    @Scheduled(fixedDelay = 1000)
    private void scheduleLocationUpdate() {
        notificationService.sendVehicleLocations(new VehicleLocationNotification(NotificationType.VEHICLE_LOCATIONS.name(), this.getVehicleIndicators()));
    }
}
