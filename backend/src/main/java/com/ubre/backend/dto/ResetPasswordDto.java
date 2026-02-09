package com.ubre.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResetPasswordDto {
    private String token;
    @NotBlank(message = "New password is required")
    private String newPassword;
}
