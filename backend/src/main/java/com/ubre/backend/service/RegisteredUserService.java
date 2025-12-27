package com.ubre.backend.service;

import com.ubre.backend.dto.UserDto;


public interface RegisteredUserService {
    UserDto register(UserDto registerDTO);
    void activateAccount(String token);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
