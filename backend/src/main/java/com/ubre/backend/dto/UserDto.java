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

    public UserDto() {
    }

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

    public UserDto(User user) {
        this.id = user.getId();
        this.role = user.getRole();
        this.avatarUrl = user.getAvatarUrl();
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.status = user.getStatus();
    }
}
