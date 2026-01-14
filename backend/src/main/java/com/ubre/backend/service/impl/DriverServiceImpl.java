package com.ubre.backend.service.impl;

import com.ubre.backend.dto.DriverRegistrationDto;
import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.UserStats;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.DriverService;
import com.ubre.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDto> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        List<UserDto> driverDtos = new ArrayList<>();
        for (Driver driver : drivers) {
            UserDto dto = new UserDto(
                    driver.getId(),
                    Role.DRIVER,
                    driver.getAvatarUrl(),
                    driver.getEmail(),
                    driver.getName(),
                    driver.getSurname(),
                    driver.getPhone(),
                    driver.getAddress(),
                    driver.getStatus()
            );
            driverDtos.add(dto);
        }
        return driverDtos;
    }


    @Override
    public List<UserDto> getAvailableDrivers() {
        List<Driver> drivers = driverRepository.findByStatus(UserStatus.ACTIVE);
        List<UserDto> driverDtos = new ArrayList<>();
        for (Driver driver : drivers) {
            UserDto dto = new UserDto(
                    driver.getId(),
                    Role.DRIVER,
                    driver.getAvatarUrl(),
                    driver.getEmail(),
                    driver.getName(),
                    driver.getSurname(),
                    driver.getPhone(),
                    driver.getAddress(),
                    driver.getStatus()
            );
            driverDtos.add(dto);
        }
        return driverDtos;
    }

    @Override
    public UserDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        return new UserDto(
                driver.getId(),
                Role.DRIVER,
                driver.getAvatarUrl(),
                driver.getEmail(),
                driver.getName(),
                driver.getSurname(),
                driver.getPhone(),
                driver.getAddress(),
                driver.getStatus()
        );
    }

    @Override
    public void activateDriverAccount(String token, String email, String newPassword) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        if (driver.getIsActivated()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is already activated");
        }

        // error 400 - bad request
        if (!driver.getActivationToken().equals(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activation token");
        }

        // error 410 - gone
        if (driver.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Activation token has expired");
        }

        // TODO: errors such as unauthorized or forbidden

        driver.setPassword(passwordEncoder.encode(newPassword));
        driver.setIsActivated(true);
        driver.setStatus(UserStatus.INACTIVE); // Set status to INACTIVE upon activation
        driver.setActivationToken(null);
        driver.setActivationTokenExpiry(null);

        driverRepository.save(driver);
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

        boolean emailExists = userRepository.findByEmail(dto.getEmail()).isPresent();

        if (emailExists) { throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Email already exists");
        }

        Driver newDriver = new Driver();
        newDriver.setRole(Role.DRIVER);
        newDriver.setName(dto.getName());
        newDriver.setSurname(dto.getSurname());
        newDriver.setEmail(dto.getEmail());
        newDriver.setPassword(passwordEncoder.encode(dto.getPassword()));
        newDriver.setPhone(dto.getPhone());
        newDriver.setAddress(dto.getAddress());
        newDriver.setStatus(UserStatus.INACTIVE); // New drivers are inactive by default
        newDriver.setAvatarUrl(dto.getAvatarUrl());
        newDriver.setIsActivated(false);
        newDriver.setIsBlocked(false);

        String activationToken = java.util.UUID.randomUUID().toString();
        LocalDateTime activationTokenExpiry = LocalDateTime.now().plusDays(1);

        newDriver.setActivationToken(activationToken);
        newDriver.setActivationTokenExpiry(activationTokenExpiry);

        // dont forget user statistics
        UserStats userStats = new UserStats(newDriver);
        newDriver.setStats(userStats);

        Driver savedDriver = driverRepository.save(newDriver);

        emailService.sendDriverActivationEmail(savedDriver.getEmail(), activationToken);

        return new UserDto(
                savedDriver.getId(),
                savedDriver.getRole(),
                savedDriver.getAvatarUrl(),
                savedDriver.getEmail(),
                savedDriver.getName(),
                savedDriver.getSurname(),
                savedDriver.getPhone(),
                savedDriver.getAddress(),
                savedDriver.getStatus()
        );
    }

    @Override
    public RideDto notifyDriver(Long rideId, Long driverId) {
        // Mock implementation of notifying a driver about a ride
        System.out.println("Notifying driver with ID " + driverId + " about ride with ID " + rideId);
        return new RideDto();
    }

}
