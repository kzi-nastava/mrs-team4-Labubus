package com.ubre.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubre.backend.enums.ProfileChangeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "profile_changes")
public class ProfileChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @JsonIgnore
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileChangeStatus status = ProfileChangeStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String oldName;
    private String newName;

    private String oldSurname;
    private String newSurname;

    private String oldAddress;
    private String newAddress;

    private String oldPhone;
    private String newPhone;

    private String oldAvatarUrl;
    private String newAvatarUrl;
}
