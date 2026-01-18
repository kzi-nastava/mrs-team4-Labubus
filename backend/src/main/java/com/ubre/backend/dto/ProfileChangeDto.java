package com.ubre.backend.dto;

// Made by driver ---> and then is sent to admin for approval
// For changing profile data: name, surname, address, phone, avatarUrl

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public ProfileChangeDto() {

    }

    public ProfileChangeDto(String newSurname, Long requestId, Long userId, String oldName, String newName, String oldSurname, String oldAddress, String newAddress, String oldPhone, String newPhone, String oldAvatarUrl, String newAvatarUrl) {
        this.newSurname = newSurname;
        this.id = requestId;
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
}

