package com.ubre.backend.service.impl;

import com.ubre.backend.model.Ride;
import com.ubre.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

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
  
   @Override
   public void sendPassengerActivationEmail(String recipientEmail, String activationToken) {

        String activationLink = "http://localhost:8080/api/auth/activate?token=" + activationToken;
        String subject = "Activate your Ubre account";
        String body = buildPassengerActivationEmailBody(activationLink);
     
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


   @Override
   public void sendRideCompletedEmail(String recipientEmail, Ride ride) {
       String subject = "Your ride receipt";
       String body = buildRideCompletedBody(ride);

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
           throw new IllegalStateException("Failed to send ride receipt.", ex);
       }
   }



    @Override
    public void sendPasswordResetEmail(String email, String token, String userAgent) {
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        if (userAgent != null && userAgent.contains("Mobile-Android"))
            resetLink = "https://ubre.notixdms.com/reset-password?token=" + token;

        String subject = "Reset your password";
        String body = buildPasswordResetEmailBody(resetLink);
   
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("Ubre <" + fromAddress + ">");
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send activation email.", ex);
        }
    }

    @Override
    public void sendPassengerRideInvitationEmail(String recipientEmail, Ride ride) {
        String subject = "Your ride receipt";
        String body = buildPassengerRideInvitationEmailBody(ride);

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
            throw new IllegalStateException("Failed to send invitation email.", ex);
        }
    }


    private String buildPasswordResetEmailBody(String resetLink) {
        return """
    <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937; max-width:600px; margin:0 auto; background:#ffffff; padding:24px; border-radius:8px;">
      
      <h2 style="color:#111827; margin-bottom:12px;">Reset Your Password</h2>
      
      <p>
        We received a request to reset the password for your Ubre account. 
        Click the button below to choose a new password.
      </p>

      <div style="text-align:center; margin:24px 0;">
        <a href="%s"
           style="background:#2563eb; color:#ffffff; padding:12px 20px; text-decoration:none; border-radius:6px; font-weight:600; display:inline-block;">
          Reset Password
        </a>
      </div>

      <p style="font-size:14px; color:#374151;">
        For security reasons, this link will expire in 30 minutes. 
        If you did not request a password reset, please ignore this email or contact support if you have concerns.
      </p>

      <hr style="border:none; border-top:1px solid #e5e7eb; margin:24px 0;"/>

      <p style="font-size:14px; color:#6b7280;">
        Thank you,<br/>
        <strong>Ubre Team</strong>
      </p>
    </div>
    """.formatted(resetLink);
    }
     
    private String buildPassengerActivationEmailBody(String activationLink) {
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937; max-width:600px; margin:0 auto; background:#ffffff; padding:24px; border-radius:8px;">
                  
                  <h2 style="color:#111827; margin-bottom:12px;">Welcome to Ubre!</h2>
                  
                  <p>
                    Thank you for joining <strong>Ubre</strong>! To start booking rides,
                    please confirm your email address by clicking the button below.
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

    private String buildRideCompletedBody(Ride ride) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937; max-width:600px; margin:0 auto; background:#ffffff; padding:24px; border-radius:8px;">
                  
                  <h2 style="color:#111827; margin-bottom:12px;">Your ride has ended!</h2>
                  
                  <p>
                    We hope that you enjoyed your experience during the
                    ride that you found our services and services of our
                    employees satisfactory.
                  </p>
                  
                  <div style="background:#f9fafb; border:1px solid #e5e7eb; border-radius:8px; padding:16px; margin:20px 0;">
                    <h3 style="margin:0 0 12px 0; color:#111827;">Ride details</h3>
                    
                    <ul style="list-style:none; padding:0; margin:0; font-size:14px; color:#374151;">
                        <li style="display:flex; justify-content:space-between; padding:6px 0;">
                          <span>Duration: </span>
                          <strong>%s - %s</strong>
                        </li>
                        <li style="display:flex; justify-content:space-between; padding:6px 0;">
                          <span>Distance: </span>
                          <strong>%.2f m</strong>
                        </li>
                        <li style="display:flex; justify-content:space-between; padding:6px 0; border-top:1px solid #e5e7eb; margin-top:8px;">
                          <span>Total price: </span>
                          <strong>$%.2f</strong>
                        </li>
                      </ul>
                  </div>

                  <p style="font-size:14px; color:#374151;">
                    We value the opinion of our customers so if there is
                    any feedback you wish to leave fell free to do so
                    through reviews in ride history of our app.
                  </p>

                  <hr style="border:none; border-top:1px solid #e5e7eb; margin:24px 0;"/>

                  <p style="font-size:14px; color:#6b7280;">
                    Thank you,<br/>
                    <strong>Ubre Team</strong>
                  </p>
                </div>
                """.formatted(ride.getStartTime().format(fmt), ride.getEndTime().format(fmt), ride.getDistance(), ride.getPrice());
    }

    private String buildPassengerRideInvitationEmailBody(Ride ride) {
        String creatorFullName = ride.getCreator().getName() + " " + ride.getCreator().getSurname();
        String creatorEmail = ride.getCreator().getEmail();

        String startingLocation = ride.getWaypoints().get(0).getLabel();
        startingLocation = startingLocation.length() >= 30 ? startingLocation.substring(0, 27) + "..." : startingLocation;

        String endLocation = ride.getWaypoints().get(ride.getWaypoints().size()-1).getLabel();
        endLocation = endLocation.length() >= 30 ? endLocation.substring(0, 27) + "..." : endLocation;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        String startTime = ride.getStartTime().format(fmt);

        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937; max-width:600px; margin:0 auto; background:#ffffff; padding:24px; border-radius:8px;">
                  
                  <h2 style="color:#111827; margin-bottom:12px;">You have been invited to a ride!</h2>
                  
                  <p>
                    You were invited to a ride by %s (%s)! To see details about the ride
                    like the starting location and time checkout the ride details below.
                  </p>
                  
                  <div style="background:#f9fafb; border:1px solid #e5e7eb; border-radius:8px; padding:16px; margin:20px 0;">
                    <h3 style="margin:0 0 12px 0; color:#111827;">Ride details</h3>
                    
                    <ul style="list-style:none; padding:0; margin:0; font-size:14px; color:#374151;">
                        <li style="display:flex; justify-content:space-between; padding:6px 0;">
                          <span>From: </span>
                          <strong>%s</strong>
                        </li>
                        <li style="display:flex; justify-content:space-between; padding:6px 0;">
                          <span>To: </span>
                          <strong>%s</strong>
                        </li>
                        <li style="display:flex; justify-content:space-between; padding:6px 0; border-top:1px solid #e5e7eb; margin-top:8px;">
                          <span>Start: </span>
                          <strong>%s</strong>
                        </li>
                      </ul>
                  </div>
                  
                  <p>
                    Once it starts you can track the ride by logging on to your
                    account (follow the link below to the website).
                  </p>

                  <div style="text-align:center; margin:24px 0;">
                    <a href="http://localhost:4200"
                       style="background:#2563eb; color:#ffffff; padding:12px 20px; text-decoration:none; border-radius:6px; font-weight:600; display:inline-block;">
                      Track ride
                    </a>
                  </div>

                  <p style="font-size:14px; color:#374151;">
                    If you believe this invitation was a mistake fell free to
                    ignore this message until the ride ends or decline the invitation.
                  </p>

                  <hr style="border:none; border-top:1px solid #e5e7eb; margin:24px 0;"/>

                  <p style="font-size:14px; color:#6b7280;">
                    Hope you enjoy your ride,<br/>
                    <strong>Ubre Team</strong>
                  </p>
                </div>
                """.formatted(creatorFullName, creatorEmail, startingLocation, endLocation, startTime);
    }
}
