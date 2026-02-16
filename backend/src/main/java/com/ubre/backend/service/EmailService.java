package com.ubre.backend.service;

import com.ubre.backend.model.Ride;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendDriverActivationEmail(String recipientEmail, String activationToken);
    @Async
    void sendPasswordResetEmail(String email, String token, String userAgent);
    @Async
    void sendRideCompletedEmail(String recipientEmail, Ride ride);
    @Async
    void sendPassengerActivationEmail(String recipientEmail, String activationToken);
    @Async
    void sendPassengerRideInvitationEmail(String recipientEmail, Ride ride);
}
