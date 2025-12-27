package com.ubre.backend.service;

import com.ubre.backend.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto updateUserDto);
    void deleteUser(Long id);
    void blockUser(Long id);
    void unblockUser(Long id);
}
