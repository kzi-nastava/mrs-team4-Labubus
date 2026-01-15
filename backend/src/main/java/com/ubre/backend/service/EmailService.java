package com.ubre.backend.service;

public interface EmailService {
    void sendDriverActivationEmail(String recipientEmail, String activationToken);
}
