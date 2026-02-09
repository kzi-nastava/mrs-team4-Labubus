package com.ubre.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO for passenger registrations
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    private String avatarUrl;
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "First name is required")
    private String name;
    @NotBlank(message = "Last name is required")
    private String surname;
    @NotBlank(message = "Phone number is required")
    private String phone;
    @NotBlank(message = "Address is required")
    private String address;

}
