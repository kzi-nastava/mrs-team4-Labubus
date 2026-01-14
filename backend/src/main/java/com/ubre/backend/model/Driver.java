package com.ubre.backend.model;

import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("DRIVER")
public class Driver extends User {

    @Column(name = "activation_token")
    private String activationToken;

    @Column(name = "activation_token_expiry")
    private LocalDateTime activationTokenExpiry;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    private Boolean pendingInactiveStatus = false;

    // Constructors
    public Driver() {
        super();
    }

    public Driver(Role role, String email, String password, String name, String surname, String address, String phone, String avatarUrl, UserStatus status, Boolean isActivated, Boolean isBlocked, String activationToken, LocalDateTime activationTokenExpiry, Vehicle vehicle) {
        super(role, email, password, name, surname, address, phone, avatarUrl, status, isActivated, isBlocked);
        this.activationToken = activationToken;
        this.activationTokenExpiry = activationTokenExpiry;
        this.vehicle = vehicle;
    }
}
