
package com.ubre.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubre.backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_status_records")
public class UserStatusRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    public UserStatusRecord() {}

    public UserStatusRecord(User user, UserStatus status, LocalDateTime validFrom) {
        this.user = user;
        this.status = status;
        this.validFrom = validFrom;
    }
}
