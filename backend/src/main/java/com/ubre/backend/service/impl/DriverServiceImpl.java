package com.ubre.backend.service.impl;

import com.ubre.backend.dto.DriverRegistrationDto;
import com.ubre.backend.dto.ProfileChangeDto;
import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.ProfileChangeStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.ProfileChange;
import com.ubre.backend.model.UserStats;
import com.ubre.backend.model.Vehicle;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.repository.VehicleRepository;
import com.ubre.backend.service.DriverService;
import com.ubre.backend.service.EmailService;
import com.ubre.backend.websocket.ProfileChangeNotification;
import com.ubre.backend.websocket.WebSocketNotificationService;
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

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

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

        boolean vehiclePlatesExists = vehicleRepository.findByPlates(dto.getVehicle().getPlates()).isPresent();
        if (vehiclePlatesExists) { throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Vehicle with the same plates already exists");
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

        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(newDriver);
        vehicle.setModel(dto.getVehicle().getModel());
        vehicle.setPlates(dto.getVehicle().getPlates());
        vehicle.setType(dto.getVehicle().getType());
        vehicle.setSeats(dto.getVehicle().getSeats());
        vehicle.setBabyFriendly(dto.getVehicle().getBabyFriendly());
        vehicle.setPetFriendly(dto.getVehicle().getPetFriendly());

        newDriver.setVehicle(vehicle);

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

    @Override
    public void requestProfileChange(ProfileChangeDto profileChangeDto) {
        Long driverId = profileChangeDto.getUserId();
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        ProfileChange profileChange = new ProfileChange();
        profileChange.setDriver(driver);

        profileChange.setOldName(driver.getName());
        profileChange.setNewName(profileChangeDto.getNewName());

        profileChange.setOldSurname(driver.getSurname());
        profileChange.setNewSurname(profileChangeDto.getNewSurname());

        profileChange.setOldPhone(driver.getPhone());
        profileChange.setNewPhone(profileChangeDto.getNewPhone());

        profileChange.setOldAddress(driver.getAddress());
        profileChange.setNewAddress(profileChangeDto.getNewAddress());

        profileChange.setOldAvatarUrl(driver.getAvatarUrl());
        profileChange.setNewAvatarUrl(profileChangeDto.getNewAvatarUrl());

        profileChange.setCreatedAt(LocalDateTime.now());

        driver.getProfileChanges().add(profileChange);
        driverRepository.save(driver);
    }

    @Override
    public List<ProfileChangeDto> getPendingProfileChanges() {
        List<Driver> drivers = driverRepository.findAll();
        List<ProfileChangeDto> pendingChanges = new ArrayList<>();

        for (Driver driver : drivers) {
            for (ProfileChange change : driver.getProfileChanges()) {
                if (change.getStatus() == ProfileChangeStatus.PENDING) {
                    ProfileChangeDto dto = new ProfileChangeDto();

                    dto.setId(change.getId());
                    dto.setUserId(driver.getId());
                    dto.setOldName(change.getOldName());
                    dto.setNewName(change.getNewName());
                    dto.setOldSurname(change.getOldSurname());
                    dto.setNewSurname(change.getNewSurname());
                    dto.setOldPhone(change.getOldPhone());
                    dto.setNewPhone(change.getNewPhone());
                    dto.setOldAddress(change.getOldAddress());
                    dto.setNewAddress(change.getNewAddress());
                    dto.setOldAvatarUrl(change.getOldAvatarUrl());
                    dto.setNewAvatarUrl(change.getNewAvatarUrl());

                    pendingChanges.add(dto);
                }
            }
        }

        return pendingChanges;
    }

    // approve profile change
    @Override
    public void approveProfileChange(Long profileChangeId) {
        ProfileChange profileChange = null;
        Driver driver = null;

        List<Driver> drivers = driverRepository.findAll();
        for (Driver d : drivers) {
            for (ProfileChange pc : d.getProfileChanges()) {
                if (pc.getId().equals(profileChangeId)) {
                    profileChange = pc;
                    driver = d;
                    break;
                }
            }
            if (profileChange != null) {
                break;
            }
        }

        if (profileChange == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile change request not found");
        }

        // Apply changes to driver
        driver.setName(profileChange.getNewName());
        driver.setSurname(profileChange.getNewSurname());
        driver.setPhone(profileChange.getNewPhone());
        driver.setAddress(profileChange.getNewAddress());
        driver.setAvatarUrl(profileChange.getNewAvatarUrl());

        // Update profile change status
        profileChange.setStatus(ProfileChangeStatus.APPROVED);

        driverRepository.save(driver); // by saving driver, profileChange is also saved because of cascade

        webSocketNotificationService.sendProfileChangeApproved(driver.getId(), new ProfileChangeNotification(
                ProfileChangeStatus.APPROVED.name(),
                new UserDto(
                driver.getId(),
                driver.getRole(),
                driver.getAvatarUrl(),
                driver.getEmail(),
                driver.getName(),
                driver.getSurname(),
                driver.getPhone(),
                driver.getAddress(),
                driver.getStatus()
        )));
    }

    // reject profile change
    @Override
    public void rejectProfileChange(Long profileChangeId) {
        ProfileChange profileChange = null;
        Driver driver = null;
        List<Driver> drivers = driverRepository.findAll();
        for (Driver d : drivers) {
            for (ProfileChange pc : d.getProfileChanges()) {
                if (pc.getId().equals(profileChangeId)) {
                    profileChange = pc;
                    driver = d;
                    break;
                }
            }
            if (profileChange != null) {
                break;
            }
        }
        if (profileChange == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile change request not found");
        }

        // Update profile change status
        profileChange.setStatus(ProfileChangeStatus.REJECTED);
        driverRepository.save(driver); // by saving driver, profileChange is also saved because of cascade

        webSocketNotificationService.sendProfileChangeRejected(driver.getId(), new ProfileChangeNotification(
                ProfileChangeStatus.REJECTED.name(),
                null
        ));
    }
}
