package com.ubre.backend.service.impl;

import com.ubre.backend.dto.DriverRegistrationDto;
import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    // Mock List only for testing purposes
    private final List<UserDto> drivers = new ArrayList<UserDto>();

    public DriverServiceImpl() {
        drivers.add(new UserDto(1L, Role.DRIVER, "avatarUrl1", "driver1@ubre.com", "John", "Wick", "0123456789", "Crazy street 1", UserStatus.ACTIVE));
        drivers.add(new UserDto(2L, Role.DRIVER, "avatarUrl2", "driver2@ubre.com", "Jane", "Doe", "9876543210", "Mysterious avenue 2", UserStatus.ON_RIDE));
        drivers.add(new UserDto(3L, Role.DRIVER, "avatarUrl3", "driver3@ubre.com", "Bob", "Smith", "5555555555", "Hidden boulevard 3", UserStatus.INACTIVE));
    }

    @Override
    public List<UserDto> getAllDrivers() {
        return drivers;
    }

    @Override
    public List<UserDto> getAvailableDrivers() {
        return drivers; // In a real implementation, filter by availability
    }

    @Override
    public UserDto getDriverById(Long id) {
        return drivers.stream()
                .filter(driver -> driver.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
    }

    @Override
    public void toggleAvailability(Long id) {
        UserDto driver = getDriverById(id);

        if (driver.getStatus() == UserStatus.ON_RIDE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot change availability while on a ride");
        }

        driver.setStatus(driver.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);
    }

    @Override
    public void approveProfileChanges(Long id) {
        System.out.println("Profile changes for driver with ID " + id + " have been approved.");
    }

    @Override
    public void rejectProfileChanges(Long id) {
        System.out.println("Profile changes for driver with ID " + id + " have been rejected.");
    }

    @Override
    public UserDto createDriver(DriverRegistrationDto dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        boolean emailExists = drivers.stream()
                .anyMatch(d -> d.getEmail().equalsIgnoreCase(dto.getEmail()));

        if (emailExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        Long newId = (long) (drivers.size() + 1);

        UserDto newDriver = new UserDto(
                newId,
                Role.DRIVER,
                dto.getAvatarUrl(),
                dto.getEmail(),
                dto.getName(),
                dto.getSurname(),
                dto.getPhone(),
                dto.getAddress(),
                UserStatus.INACTIVE
        );

        drivers.add(newDriver);
        System.out.println("New driver created: " + newDriver.getEmail());
        return newDriver;
    }

    @Override
    public RideDto notifyDriver(Long rideId, Long driverId) {
        // Mock implementation of notifying a driver about a ride
        System.out.println("Notifying driver with ID " + driverId + " about ride with ID " + rideId);
        return new RideDto();
    }

}