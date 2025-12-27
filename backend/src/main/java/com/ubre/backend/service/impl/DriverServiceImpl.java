package com.ubre.backend.service.impl;

import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.User;
import com.ubre.backend.service.DriverService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class DriverServiceImpl implements DriverService {

    // Mock collection only for testing purposes
    private final Collection<UserDto> drivers = new ArrayList<UserDto>();

    public DriverServiceImpl() {
        drivers.add(new UserDto(1L, Role.DRIVER, "avatarUrl1", "driver1@ubre.com", "John", "Wick", "0123456789", "Crazy street 1", UserStatus.ACTIVE));
        drivers.add(new UserDto(2L, Role.DRIVER, "avatarUrl2", "driver2@ubre.com", "Jane", "Doe", "9876543210", "Mysterious avenue 2", UserStatus.ON_RIDE));
        drivers.add(new UserDto(3L, Role.DRIVER, "avatarUrl3", "driver3@ubre.com", "Bob", "Smith", "5555555555", "Hidden boulevard 3", UserStatus.INACTIVE));
    }

    @Override
    public Collection<UserDto> getAllDrivers() {
        return drivers;
    }

    @Override
    public Collection<UserDto> getAvailableDrivers() {
        return drivers; // In a real implementation, filter by availability
    }

    @Override
    public UserDto getDriverById(Long id) {
        return drivers.stream()
                .filter(driver -> driver.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    @Override
    public void toggleAvailability(Long id) {
        // Mock implementation: In a real scenario, update the driver's availability status in the database
        // Switch UserStatus between ACTIVE and INACTIVE for demonstration
        UserDto driver = getDriverById(id);
        if (driver.getStatus() == UserStatus.ACTIVE) {
            driver.setStatus(UserStatus.INACTIVE);
        } else {
            driver.setStatus(UserStatus.ACTIVE);
        }
    }

    @Override
    public void approveProfileChanges(Long id) {
        // Mock implementation: In a real scenario, update the driver's profile change approval status in the database
        // For demonstration, we can just print a message
        System.out.println("Profile changes for driver with ID " + id + " have been approved.");
    }

    @Override
    public void rejectProfileChanges(Long id) {
        // Mock implementation: In a real scenario, update the driver's profile change approval status in the database
        // For demonstration, we can just print a message
        System.out.println("Profile changes for driver with ID " + id + " have been rejected.");
    }
}