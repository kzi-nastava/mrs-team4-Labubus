package com.ubre.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    private String email;
    private String passwordHash;

    public LoginDto() {
    }

    public LoginDto(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}