package com.example.ubre.ui.dtos;

// DTO for ride cancellation requests

public class CancellationDto {
    private String reason;

    public CancellationDto() {
    }

    public CancellationDto(String reason) {
        this.reason = reason;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
