package com.ubre.backend.dto;

import java.time.LocalDateTime;

// DTO for querying rides with sorting and filtering options

public class RideQueryDto {
    private Long userId;
    private String sortBy;
    private Boolean ascending;
    private LocalDateTime date;

    public RideQueryDto() {
    }
    public RideQueryDto(Long userId, String sortBy, boolean ascending, LocalDateTime date) {
        this.userId = userId;
        this.sortBy = sortBy;
        this.ascending = ascending;
        this.date = date;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean isAscending() {
        return ascending;
    }

    public void setAscending(Boolean ascending) {
        this.ascending = ascending;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
