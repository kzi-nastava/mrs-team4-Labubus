package com.ubre.backend.dto;

import lombok.Getter;
import lombok.Setter;

// DTO for passenger registrations
@Getter
@Setter
public class UserRegistrationDto {
    private String avatarUrl;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String phone;
    private String address;

    public UserRegistrationDto() {
    }

    public UserRegistrationDto(String avatarUrl, String email, String password, String name, String surname, String phone, String address) {
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
    }
}
