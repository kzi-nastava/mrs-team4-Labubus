package com.ubre.backend.service.impl;

import com.ubre.backend.dto.PasswordChangeDto;
import com.ubre.backend.dto.ProfileChangeDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.UserStatsDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    // Mock data for demonstration purposes
    private static List<UserDto> users = new ArrayList<UserDto>();

    public UserServiceImpl() {
        users.add(new UserDto(1L, Role.DRIVER, "avatar1.png", "john@doe.com", "John", "Doe", "1234567890", "123 Main St", UserStatus.ACTIVE));
        users.add(new UserDto(2L, Role.REGISTERED_USER, "avatar2.png", "jane@doe.com", "Jane", "Doe", "0987654321", "456 Elm St", UserStatus.INACTIVE));
        users.add(new UserDto(3L, Role.DRIVER, "avatar3.png", "king@cobra.com", "King", "Cobra", "1122334455", "789 Oak St", UserStatus.ON_RIDE));
    }

    @Override
    public UserDto getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users;
    }

    @Override
    public UserDto updateUser(ProfileChangeDto profileChangeDto) {
        UserDto user = getUserById(profileChangeDto.getUserId());
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
        UserDto user = getUserById(passwordChangeDto.getUserId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // todo: implement password change logic
    }
}
