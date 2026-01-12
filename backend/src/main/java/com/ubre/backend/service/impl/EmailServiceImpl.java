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
        String activationLink = buildActivationLink(activationToken);
        String subject = "Aktivacija naloga vozača";
        String body = buildDriverActivationEmailBody(activationLink);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(fromAddress);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send activation email.", ex);
        }
    }

    private String buildActivationLink(String activationToken) {
        String separator = activationLinkBaseUrl.contains("?") ? "&" : "?";
        return activationLinkBaseUrl + separator + "token=" + activationToken;
    }

    private String buildDriverActivationEmailBody(String activationLink) {
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937;">
                  <h2 style="color:#111827; margin-bottom: 8px;">Dobro došli u Ubre!</h2>
                  <p>Administrator je upravo kreirao vaš nalog za vozača. Da biste aktivirali profil i postavili lozinku, kliknite na link ispod.</p>
                  <p style="margin: 16px 0;">
                    <a href="%s" style="background:#2563eb; color:#ffffff; padding:10px 16px; text-decoration:none; border-radius:6px;">
                      Aktiviraj nalog
                    </a>
                  </p>
                  <p>Link je jednokratan i važi 24 časa. Ukoliko niste zatražili nalog, ignorišite ovu poruku.</p>
                  <p>Hvala,<br/>Ubre tim</p>
                </div>
                """.formatted(activationLink);
    }
}
