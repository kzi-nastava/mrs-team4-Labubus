package com.ubre.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PanicNotification {
    @Id
    private Long id;
    private Long rideId;
    private String triggeredBy;
    private LocalDateTime timestamp;
}
