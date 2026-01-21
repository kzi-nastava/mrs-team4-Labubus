package com.ubre.backend.service.impl;

import com.ubre.backend.dto.*;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.ActivationTokenRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.EmailService;
import com.ubre.backend.repository.UserStatusRecordRepository;
import com.ubre.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_AVATAR_URL = "default.png";
    // Mock data for demonstration purposes
    private static List<UserDto> users = new ArrayList<UserDto>();
    private static List<ProfileChangeDto> profileChangeRequests = new ArrayList<>();
    private static List<String> passengerRequests = new ArrayList<>();

    public UserServiceImpl() {
        users.add(new UserDto(1L, Role.DRIVER, "avatar1.png", "john@doe.com", "John", "Doe", "1234567890", "123 Main St", UserStatus.ACTIVE));
        users.add(new UserDto(2L, Role.REGISTERED_USER, "avatar2.png", "jane@doe.com", "Jane", "Doe", "0987654321", "456 Elm St", UserStatus.INACTIVE));
        users.add(new UserDto(3L, Role.DRIVER, "avatar3.png", "king@cobra.com", "King", "Cobra", "1122334455", "789 Oak St", UserStatus.ON_RIDE));
    }

    // real repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivationTokenRepository tokenRepository;

    @Autowired
    private UserStatusRecordRepository userStatusRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public Resource getAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Avatar not found");
        }

        if (!avatarUrl.toLowerCase().endsWith(".jpg")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported avatar format");
        }

        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = root.resolve(avatarUrl).normalize();

        if (!filePath.startsWith(root)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid avatar path");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Avatar not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid avatar URL");
        }
    }

    @Override
    public UserDto registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        User user = new Passenger();
        if (dto.getAvatarUrl() == null || dto.getAvatarUrl().isBlank()) {
            user.setAvatarUrl(DEFAULT_AVATAR_URL);
        } else {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setCreatedAt(LocalDateTime.now());
        user.setIsBlocked(false);
        user.setIsActivated(false);
        User savedUser = userRepository.save(user);



        ActivationToken token = new ActivationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));

        tokenRepository.save(token);

        emailService.sendPassengerActivationEmail(savedUser.getEmail(), token.getToken());
        return new UserDto(savedUser);
    }

    // avater upload
    @Override
    public void uploadAvatar(Long userId, MultipartFile avatar) {
        String filename = avatar.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".jpg")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only .jpg files are allowed");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);

            // avatar filename should be unique per user, add user email in front of filename
            filename = user.getEmail() + "_" + filename;
            // delete old if exists (not null and not default-avatar.jpg)
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty() && !user.getAvatarUrl().equals("default-avatar.jpg")) {
                Path oldFilePath = root.resolve(user.getAvatarUrl()).normalize();
                Files.deleteIfExists(oldFilePath);
            }

            // set filename as avatarUrl in user entity
            user.setAvatarUrl(filename);
            userRepository.save(user);

            Path filePath = root.resolve(filename).normalize();

            Files.copy(avatar.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store avatar file");
        }
    }


    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserDto(
                user.getId(),
                user.getRole(),
                user.getAvatarUrl(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getAddress(),
                user.getStatus()
        );
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserDto(
                user.getId(),
                user.getRole(),
                user.getAvatarUrl(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getAddress(),
                user.getStatus()
        );
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users;
    }

//    @Override
//    public UserDto updateUser(ProfileChangeDto profileChangeDto) {
//        UserDto user = getUserById(profileChangeDto.getUserId());
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//        }
//        users.remove(user);
//        UserDto updatedUser = new UserDto(
//                user.getId(),
//                user.getRole(),
//                profileChangeDto.getNewAvatarUrl(),
//                user.getEmail(),
//                profileChangeDto.getNewName(),
//                profileChangeDto.getNewSurname(),
//                profileChangeDto.getNewPhone(),
//                profileChangeDto.getNewAddress(),
//                user.getStatus()
//        );
//        users.add(updatedUser);
//        return updatedUser;
//    }

    @Override
    public void deleteUser(Long id) {
        boolean exists = userRepository.existsById(id);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    // this method is primarly used for updating user profile by the user themselves, and admin also (not for drivers)
    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        // avatarUrl is updated via uploadAvatar method
        User updatedUser = userRepository.save(user);
        return new UserDto(
                updatedUser.getId(),
                updatedUser.getRole(),
                updatedUser.getAvatarUrl(),
                updatedUser.getEmail(),
                updatedUser.getName(),
                updatedUser.getSurname(),
                updatedUser.getPhone(),
                updatedUser.getAddress(),
                updatedUser.getStatus()
        );
    }

    @Override
    public void blockUser(Long id) {
        // todo: implement block user logic
    }

    @Override
    public void unblockUser(Long id) {
        // todo: same sh
    }

    // get user stats by id
    @Override
    public UserStatsDto getUserStats(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserStats stats = user.getStats();
        if (stats == null) {
            // that means user has no stats yet, so create empty stats for them
            stats = new UserStats(user);
            user.setStats(stats);
            userRepository.save(user);
        }

        UserStatsDto statsDto = new UserStatsDto();
        statsDto.setUserId(user.getId());
        statsDto.setActivePast24Hours(stats.getActivePast24Hours()); // minutes
        statsDto.setNumberOfRides(stats.getNumberOfRides());
        statsDto.setDistanceTraveled(stats.getDistanceTraveled()); // kilometers, float
        statsDto.setMoneySpent(stats.getMoneySpent()); // float
        statsDto.setMoneyEarned(stats.getMoneyEarned()); // float

        return statsDto;
    }

    @Override
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findById(passwordChangeDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {
        UserDto user = getUserById(id); // here working later with concrete model
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // todo: implement activate user logic
    }

    @Override
    public void requestProfileChange(ProfileChangeDto profileChangeDto) {
        profileChangeRequests.add(profileChangeDto);
    }

    @Override
    public List<ProfileChangeDto> getAllProfileChangeRequests() {
        return profileChangeRequests;
    }

    @Override
    public void sendPassengerRequest(Long id, String passengerEmail) {
        UserDto user = getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // todo: implement send passenger request logic via email
    }

    @Override
    public UserDto createAdmin(UserDto adminDto) {
        boolean exists = userRepository.findByEmail(adminDto.getEmail()).isPresent();
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
        }

        Admin newAdmin = new Admin();
        newAdmin.setRole(Role.ADMIN);
        newAdmin.setName(adminDto.getName());
        newAdmin.setSurname(adminDto.getSurname());
        newAdmin.setEmail(adminDto.getEmail());
        newAdmin.setPassword(passwordEncoder.encode("admin123")); // default password, should be changed later
        newAdmin.setPhone(adminDto.getPhone());
        newAdmin.setAddress(adminDto.getAddress());
        newAdmin.setStatus(UserStatus.INACTIVE); // new admin is inactive by default
        newAdmin.setAvatarUrl(adminDto.getAvatarUrl());
        newAdmin.setIsActivated(true);
        newAdmin.setIsBlocked(false);

        // user stats
        UserStats stats = new UserStats(newAdmin);
        newAdmin.setStats(stats);

        Admin savedAdmin = userRepository.save(newAdmin);

        return new UserDto(
                savedAdmin.getId(),
                savedAdmin.getRole(),
                savedAdmin.getAvatarUrl(),
                savedAdmin.getEmail(),
                savedAdmin.getName(),
                savedAdmin.getSurname(),
                savedAdmin.getPhone(),
                savedAdmin.getAddress(),
                savedAdmin.getStatus()
        );
    }

    // this is only for recording user status, not for updating user status
    @Override
    public void recordUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserStatusRecord statusRecord = new UserStatusRecord();
        statusRecord.setUser(user);
        statusRecord.setStatus(status);
        statusRecord.setValidFrom(LocalDateTime.now());
        userStatusRecordRepository.save(statusRecord);
    }
}
