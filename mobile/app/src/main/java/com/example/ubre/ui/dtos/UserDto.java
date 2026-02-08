package com.example.ubre.ui.dtos;

import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.enums.UserStatus;

import java.io.Serializable;

// User...

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

    // copy constructor
    public UserDto(UserDto other) {
        this.id = other.id;
        this.role = other.role;
        this.avatarUrl = other.avatarUrl;
        this.email = other.email;
        this.name = other.name;
        this.surname = other.surname;
        this.phone = other.phone;
        this.address = other.address;
        this.status = other.status;
    }

    // getters
    public Long getId() {
        return id;
    }
    public Role getRole() {
        return role;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }

    // setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public UserStatus getStatus() {
        return status;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
