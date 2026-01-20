package com.ubre.backend.service;

public interface EmailService {
    void sendDriverActivationEmail(String recipientEmail, String activationToken);
    void sendPasswordResetEmail(String email, String token);

    void sendPassengerActivationEmail(String recipientEmail, String activationToken);
}
