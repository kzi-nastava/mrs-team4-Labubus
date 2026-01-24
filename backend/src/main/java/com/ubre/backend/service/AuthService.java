package com.ubre.backend.service;

import com.ubre.backend.dto.ResetPasswordDto;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.User;
import org.apache.coyote.BadRequestException;

import java.util.Optional;

public interface AuthService {
     User save(User user);
     User updateUserStatus(User user, UserStatus newStatus);
    public void logout(String email) throws BadRequestException;
    String toggleAvailability(String email);
    void activateAccount(String token) throws BadRequestException;
    Optional<User> findByEmail(String trim);
    void createPasswordResetToken(String email);
    void resetPassword(ResetPasswordDto dto);
}
