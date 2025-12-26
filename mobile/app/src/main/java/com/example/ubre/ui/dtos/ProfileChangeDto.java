package com.example.ubre.ui.dtos;

public class ProfileChangeDto {
    public String requestId;
    public String userId;

    public String oldName;
    public String newName;

    public String oldSurname;
    public String newSurname;

    public String oldAddress;
    public String newAddress;

    public String oldPhone;
    public String newPhone;

    public String oldAvatarUrl;
    public String newAvatarUrl;

    public ProfileChangeDto() {

    }

    public ProfileChangeDto(String newSurname, String requestId, String userId, String oldName, String newName, String oldSurname, String oldAddress, String newAddress, String oldPhone, String newPhone, String oldAvatarUrl, String newAvatarUrl) {
        this.newSurname = newSurname;
        this.requestId = requestId;
        this.userId = userId;
        this.oldName = oldName;
        this.newName = newName;
        this.oldSurname = oldSurname;
        this.oldAddress = oldAddress;
        this.newAddress = newAddress;
        this.oldPhone = oldPhone;
        this.newPhone = newPhone;
        this.oldAvatarUrl = oldAvatarUrl;
        this.newAvatarUrl = newAvatarUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getOldSurname() {
        return oldSurname;
    }

    public void setOldSurname(String oldSurname) {
        this.oldSurname = oldSurname;
    }

    public String getNewSurname() {
        return newSurname;
    }

    public void setNewSurname(String newSurname) {
        this.newSurname = newSurname;
    }

    public String getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(String oldAddress) {
        this.oldAddress = oldAddress;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getOldPhone() {
        return oldPhone;
    }

    public void setOldPhone(String oldPhone) {
        this.oldPhone = oldPhone;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }

    public String getOldAvatarUrl() {
        return oldAvatarUrl;
    }

    public void setOldAvatarUrl(String oldAvatarUrl) {
        this.oldAvatarUrl = oldAvatarUrl;
    }

    public String getNewAvatarUrl() {
        return newAvatarUrl;
    }

    public void setNewAvatarUrl(String newAvatarUrl) {
        this.newAvatarUrl = newAvatarUrl;
    }
}

