package com.ubre.backend.dto;

import java.io.Serializable;

import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// User...

@Getter
@Setter
@NoArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    @NotNull(message = "Role cannot be null")
    private Role role;
    private String avatarUrl;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email format is invalid")
    private String email;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    private String surname;
    @NotBlank(message = "Phone number cannot be blank")
    private String phone;
    @NotBlank(message = "Address cannot be blank")
    private String address;
    private UserStatus status;
    private Boolean isBlocked;

    public UserDto(Long id, Role role, String avatarUrl, String email, String name, String surname, String phone, String address, UserStatus status, Boolean isBlocked) {
        this.id = id;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.isBlocked = isBlocked;
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
        this.isBlocked = user.getIsBlocked();
    }
}
