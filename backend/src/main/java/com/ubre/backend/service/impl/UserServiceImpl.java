package com.ubre.backend.service.impl;

import com.ubre.backend.dto.PasswordChangeDto;
import com.ubre.backend.dto.ProfileChangeDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.UserStatsDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.User;
import com.ubre.backend.repository.AdminRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

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
            // delete old if exists (not null and not default-avatar.jpg)
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty() && !user.getAvatarUrl().equals("default-avatar.jpg")) {
                Path oldFilePath = root.resolve(user.getAvatarUrl()).normalize();
                Files.deleteIfExists(oldFilePath);
            }

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

    @Override
    public UserDto updateUser(ProfileChangeDto profileChangeDto) {
        UserDto user = getUserById(profileChangeDto.getUserId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        users.remove(user);
        UserDto updatedUser = new UserDto(
                user.getId(),
                user.getRole(),
                profileChangeDto.getNewAvatarUrl(),
                user.getEmail(),
                profileChangeDto.getNewName(),
                profileChangeDto.getNewSurname(),
                profileChangeDto.getNewPhone(),
                profileChangeDto.getNewAddress(),
                user.getStatus()
        );
        users.add(updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        UserDto user = getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        users.remove(user);
    }

    @Override
    public void blockUser(Long id) {
        // todo: implement block user logic
    }

    @Override
    public void unblockUser(Long id) {
        // todo: same sh
    }

    @Override
    public UserStatsDto getUserStats(Long id) {
        UserDto user = getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // Mock stats for demonstration purposes
        return new UserStatsDto(id,450, 42, 128, 4.5, 15);
    }

    @Override
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        UserDto user = getUserById(passwordChangeDto.getUserId()); // takodje radimo sa pravim objektom koji je model ustvari, nema šta sde se primeti sada
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // todo: implement password change logic
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
}
