package com.ubre.backend.dto;

// For handling complaints made by users against drivers

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintDto {
    private Long id;
    private Long driverId;
    private Long userId;
    private String text;

    public ComplaintDto() {
    }

    public ComplaintDto(Long id, Long driverId, Long userId, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.text = text;
    }
}
