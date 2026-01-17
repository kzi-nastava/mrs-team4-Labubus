package com.ubre.backend.service.impl;

import com.ubre.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String activationLinkBaseUrl;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.from}") String fromAddress,
            @Value("${app.activation-link-base-url}") String activationLinkBaseUrl
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.activationLinkBaseUrl = activationLinkBaseUrl;
    }

    @Override
    public void sendDriverActivationEmail(String recipientEmail, String activationToken) {
        String activationLink = buildActivationLink(activationToken, recipientEmail);
        String subject = "Activate your driver account";
        String body = buildDriverActivationEmailBody(activationLink);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("Ubre <" + fromAddress + ">");
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send activation email.", ex);
        }
    }

    private String buildActivationLink(String token, String email) {
        String separator = activationLinkBaseUrl.contains("?") ? "&" : "?";
        return activationLinkBaseUrl
                + separator + "token=" + token
                + "&email=" + email;
    }


    private String buildDriverActivationEmailBody(String activationLink) {
        return """
        <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937; max-width:600px; margin:0 auto; background:#ffffff; padding:24px; border-radius:8px;">
          
          <h2 style="color:#111827; margin-bottom:12px;">Welcome to Ubre!</h2>
          
          <p>
            An administrator has just created your driver account.
            To activate your profile and set your password, please click the button below.
          </p>

          <div style="text-align:center; margin:24px 0;">
            <a href="%s"
               style="background:#2563eb; color:#ffffff; padding:12px 20px; text-decoration:none; border-radius:6px; font-weight:600; display:inline-block;">
              Activate Account
            </a>
          </div>

          <p style="font-size:14px; color:#374151;">
            This link can be used only once and is valid for 24 hours.
            If you did not request this account, you can safely ignore this email.
          </p>

          <hr style="border:none; border-top:1px solid #e5e7eb; margin:24px 0;"/>

          <p style="font-size:14px; color:#6b7280;">
            Thank you,<br/>
            <strong>Ubre Team</strong>
          </p>
        </div>
        """.formatted(activationLink);
    }
}
