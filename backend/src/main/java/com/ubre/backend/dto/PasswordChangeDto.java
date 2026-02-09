package com.ubre.backend.dto;

// After driver registration for changing password and for every user to change password

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {
    public Long userId;
    @NotBlank(message = "New password cannot be blank")
    public String newPassword;

    public PasswordChangeDto() {
    }
    public PasswordChangeDto(Long userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }
}
