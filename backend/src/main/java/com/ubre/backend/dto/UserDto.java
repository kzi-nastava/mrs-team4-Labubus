package com.ubre.backend.dto;

import java.io.Serializable;

import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// User...

@Getter
@Setter
@NoArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    private Role role;
    private String avatarUrl;
    private String email;
    private String name;
    private String surname;
    private String phone;
    private String address;
    private UserStatus status;

    public UserDto(Long id, Role role, String avatarUrl, String email, String name, String surname, String phone, String address, UserStatus status) {
        this.id = id;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.status = status;
    }

    public UserDto(User model) {
        this.id = model.getId();
        this.role = model.getRole();
        this.avatarUrl = model.getAvatarUrl();
        this.email = model.getEmail();
        this.name = model.getName();
        this.surname = model.getSurname();
        this.phone = model.getPhone();
        this.address = model.getAddress();
        this.status = model.getStatus();
    }
}
