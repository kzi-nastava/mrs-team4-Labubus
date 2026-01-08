package com.ubre.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, insertable = false, updatable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(nullable = false)
    private String address;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "avatar")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "is_activated")
    private Boolean isActivated = false;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Ride> rides;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Chat chat;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(Role role, String email, String password, String name, String surname, String address, String phone, String avatarUrl, UserStatus status, Boolean isActivated, Boolean isBlocked) {
        this.role = role;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.isActivated = isActivated;
        this.isBlocked = isBlocked;
        this.createdAt = LocalDateTime.now();
    }
}
