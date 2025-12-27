package com.ubre.backend.service;

import com.ubre.backend.model.User;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.ProfileChangeDto;
import org.springframework.context.annotation.Profile;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();
    UserDto updateUser(ProfileChangeDto profileChangeDto);
    void deleteUser(Long id);
    void blockUser(Long id);
    void unblockUser(Long id);
}
