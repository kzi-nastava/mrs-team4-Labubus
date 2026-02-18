package com.example.ubre.ui.notifications;

import com.example.ubre.ui.dtos.VehicleIndicatorDto;
import com.example.ubre.ui.enums.NotificationType;

import java.io.Serializable;
import java.util.List;

public class VehicleLocationNotification implements Serializable {
    private NotificationType status;
    private List<VehicleIndicatorDto> indicators; // may be null

    public VehicleLocationNotification() {
    }

    public VehicleLocationNotification(NotificationType status, List<VehicleIndicatorDto> indicators) {
        this.status = status;
        this.indicators = indicators;
    }

    public NotificationType getStatus() {
        return status;
    }

    public void setStatus(NotificationType status) {
        this.status = status;
    }

    public List<VehicleIndicatorDto> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<VehicleIndicatorDto> indicators) {
        this.indicators = indicators;
    }

    @Override
    public String toString() {
        return "VehicleLocationNotification{" +
                "status=" + status +
                ", indicators=" + indicators +
                '}';
    }
}

