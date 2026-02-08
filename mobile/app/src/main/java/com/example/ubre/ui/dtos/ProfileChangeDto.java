package com.example.ubre.ui.dtos;

// Made by driver ---> and then is sent to admin for approval
// For changing profile data: name, surname, address, phone, avatarUrl

import com.example.ubre.ui.enums.ProfileChangeStatus;

public class ProfileChangeDto {
    public Long id;
    public Long userId;

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
    public ProfileChangeStatus profileChangeStatus;

    public ProfileChangeDto() {

    }

    public ProfileChangeDto(Long id, Long userId, String oldName, String newName, String oldSurname, String newSurname,
                            String oldAddress, String newAddress, String oldPhone, String newPhone,
                            String oldAvatarUrl, String newAvatarUrl, ProfileChangeStatus profileChangeStatus) {
        this.id = id;
        this.userId = userId;
        this.oldName = oldName;
        this.newName = newName;
        this.oldSurname = oldSurname;
        this.newSurname = newSurname;
        this.oldAddress = oldAddress;
        this.newAddress = newAddress;
        this.oldPhone = oldPhone;
        this.newPhone = newPhone;
        this.oldAvatarUrl = oldAvatarUrl;
        this.newAvatarUrl = newAvatarUrl;
        this.profileChangeStatus = profileChangeStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public ProfileChangeStatus getProfileChangeStatus() {
        return profileChangeStatus;
    }

    public void setProfileChangeStatus(ProfileChangeStatus profileChangeStatus) {
        this.profileChangeStatus = profileChangeStatus;
    }
}

