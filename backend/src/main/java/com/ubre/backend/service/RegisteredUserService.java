package com.ubre.backend.service;

import com.ubre.backend.dto.auth.RegisterDTO;
import com.ubre.backend.dto.user.UserDTO;

public interface RegisteredUserService {
    UserDTO register(RegisterDTO registerDTO);
    void activateAccount(String token);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
