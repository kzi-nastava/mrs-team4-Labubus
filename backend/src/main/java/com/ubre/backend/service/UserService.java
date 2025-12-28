package com.ubre.backend.service;

import com.ubre.backend.dto.PasswordChangeDto;
import com.ubre.backend.dto.UserStatsDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.ProfileChangeDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();
    UserDto updateUser(ProfileChangeDto profileChangeDto);
    void deleteUser(Long id);
    void blockUser(Long id);
    void unblockUser(Long id);
    UserStatsDto getUserStats(Long id);
    void changePassword(PasswordChangeDto passwordChangeDto);
    void activateUser(Long id);
}
