package com.ubre.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, insertable = false, updatable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
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

    @Column(name = "last_password_reset_date")
    private Timestamp lastPasswordResetDate;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Ride> rides;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Chat chat;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserStats stats;

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

    public User(UserDto dto) {
        this.id = dto.getId();
        this.role = dto.getRole();
        this.email = dto.getEmail();
        this.password = dto.getEmail();
        this.name = dto.getName();
        this.surname = dto.getSurname();
        this.address = dto.getAddress();
        this.phone = dto.getAddress();
        this.avatarUrl = dto.getAvatarUrl();
        this.status = dto.getStatus();
        this.isActivated = false;
        this.isBlocked = false;
        this.createdAt = LocalDateTime.now();
    }

    public Timestamp getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setPassword(String password) {
        Timestamp now = new Timestamp(new Date().getTime());
        this.setLastPasswordResetDate(now);
        this.password = password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActivated;
    }

}
