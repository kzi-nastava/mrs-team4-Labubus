package com.ubre.backend.service.impl;

import com.ubre.backend.dto.ResetPasswordDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.*;
import com.ubre.backend.service.AuthService;
import com.ubre.backend.service.EmailService;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private ActivationTokenRepository tokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserStatusRecordRepository userStatusRecordRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUserStatus(User user, UserStatus newStatus) {
        user.setStatus(newStatus);
        if (user.getRole() == Role.DRIVER) {
            UserStatusRecord statusRecord = new UserStatusRecord(user, newStatus, LocalDateTime.now());
            userStatusRecordRepository.save(statusRecord);
        }
        return userRepository.save(user);
    }

    @Override
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.ON_RIDE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot logout while on a ride!");

        updateUserStatus(user, UserStatus.INACTIVE);
    }

    @Override
    public String toggleAvailability(String email) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (driver.getStatus() == UserStatus.ON_RIDE) {
            driver.setPendingInactiveStatus(true);
            driverRepository.save(driver);
            return "You are on a ride. You will be set to INACTIVE automatically after.";
        }

        driver.setStatus(driver.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);

        UserStatusRecord statusRecord = new UserStatusRecord(driver, driver.getStatus(), LocalDateTime.now());
        userStatusRecordRepository.save(statusRecord);

        save(driver);
        return "Status changed.";
    }

    @Override
    public void activateAccount(String token) throws BadRequestException {
        ActivationToken activationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid activation token"));

        if (activationToken.getConfirmedAt() != null) {
            throw new BadRequestException("Account already activated");
        }

        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Activation token expired");
        }

        User user = activationToken.getUser();
        user.setIsActivated(true);
        userRepository.save(user);

        activationToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(activationToken);
    }

    @Override
    public Optional<User> findByEmail(String trim) {
        return userRepository.findByEmail(trim);
    }

    @Override
    @Transactional
    public void createPasswordResetToken(String email, String userAgent) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user)
                        .orElse(new PasswordResetToken());

            String token = UUID.randomUUID().toString();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

            passwordResetTokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), token, userAgent);

        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password reset token."));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has expired.");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }


}
