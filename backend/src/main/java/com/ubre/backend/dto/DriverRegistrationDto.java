package com.ubre.backend.dto;

// DTO for driver registration data

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRegistrationDto {
    private Long id;
    private String avatarUrl;
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Password cannot be null")
    private String password; // only temporary (this is going to be hashed and stored securely)
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    private String surname;
    @NotBlank(message = "Phone number cannot be blank")
    private String phone;
    @NotBlank(message = "Address cannot be blank")
    private String address;
    @NotNull(message = "Vehicle information cannot be null")
    private VehicleDto vehicle;

    public DriverRegistrationDto(Long id, String avatarUrl, String email, String password, String name, String surname, String phone, String address, VehicleDto vehicle) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.vehicle = vehicle;
    }
}
