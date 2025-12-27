package com.ubre.backend.dto;

import java.time.LocalDateTime;

// DTO for querying rides with sorting and filtering options

public class RideQueryDto {
    private int userId;
    private String sortBy;
    private boolean ascending;
    private LocalDateTime date;

    public RideQueryDto() {
    }
    public RideQueryDto(int userId, String sortBy, boolean ascending, LocalDateTime date) {
        this.userId = userId;
        this.sortBy = sortBy;
        this.ascending = ascending;
        this.date = date;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
