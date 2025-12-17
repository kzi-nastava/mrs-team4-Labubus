package com.ubre.backend.dto.user;

public class DriverDTO extends UserDTO {
    private String drivingLicense;
    private int activeHoursLast24h;
    private boolean isAvailable;
    private String currentStatus;

    public DriverDTO() {}

    public String getDrivingLicense() { return drivingLicense; }
    public void setDrivingLicense(String drivingLicense) { this.drivingLicense = drivingLicense; }
    public int getActiveHoursLast24h() { return activeHoursLast24h; }
    public void setActiveHoursLast24h(int activeHoursLast24h) { this.activeHoursLast24h = activeHoursLast24h; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
}
