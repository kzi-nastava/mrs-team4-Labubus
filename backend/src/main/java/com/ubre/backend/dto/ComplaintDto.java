package com.ubre.backend.dto;

// For handling complaints made by users against drivers

import com.ubre.backend.model.Complaint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComplaintDto {
    private Long id;
    private Long driverId;
    private Long userId;
    private String text;

    public ComplaintDto(Long id, Long driverId, Long userId, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.text = text;
    }

    public ComplaintDto(Complaint model) {
        this.id = model.getId();
        this.driverId = model.getDriver().getId();
        this.userId = model.getUser().getId();
        this.text = model.getText();
    }
}
