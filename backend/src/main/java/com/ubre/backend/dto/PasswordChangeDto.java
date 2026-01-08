package com.ubre.backend.dto;

// After driver registration for changing password and for every user to change password

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {
    public Long userId;
    public String newPassword;

    public PasswordChangeDto() {
    }
    public PasswordChangeDto(Long userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }
}
