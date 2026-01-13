package com.ubre.backend.controller;

import com.ubre.backend.service.EmailService;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    @Autowired
    EmailService emailService;

    @GetMapping("/test")
    public void testEmail(@RequestParam String email) {
        emailService.sendDriverActivationEmail(email, "12345");
    }
}
