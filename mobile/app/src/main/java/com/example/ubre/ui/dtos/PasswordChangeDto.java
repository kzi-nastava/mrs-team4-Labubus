package com.example.ubre.ui.dtos;

public class PasswordChangeDto {
    public int userId;
    public String newPassword;

    public PasswordChangeDto() {
    }
    public PasswordChangeDto(int userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
