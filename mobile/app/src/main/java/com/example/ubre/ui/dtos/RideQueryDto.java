package com.example.ubre.ui.dtos;

import java.time.LocalDateTime;

// DTO for querying rides with sorting and filtering options

public class RideQueryDto {
    private String sortBy;
    private Boolean ascending;
    private LocalDateTime date; // very questionable, fix later or convert to string

    public RideQueryDto() {
    }
    public RideQueryDto(String sortBy, Boolean ascending, LocalDateTime date) {
        this.sortBy = sortBy;
        this.ascending = ascending;
        this.date = date;
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
