package com.example.ubre.ui.dtos;

public class LoginDto {
    private String email;
    private String passwordHash;

    public LoginDto() {
    }

    public LoginDto(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
}