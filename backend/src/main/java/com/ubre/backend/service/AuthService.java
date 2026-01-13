package com.ubre.backend.service;

import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.User;
import org.apache.coyote.BadRequestException;

public interface AuthService {
     User save(User user);
     User updateUserStatus(User user, UserStatus newStatus);
    public void logout(String email) throws BadRequestException;
    String toggleAvailability(String email);
    void sendResetToken(String email);
}
