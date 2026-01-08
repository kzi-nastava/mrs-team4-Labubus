package com.ubre.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// DTO for querying rides with sorting and filtering options

@Getter
@Setter
public class RideQueryDto {
    private String sortBy;
    private Boolean ascending;
    private LocalDateTime date;

    public RideQueryDto() {
    }
    public RideQueryDto(Long userId, String sortBy, boolean ascending, LocalDateTime date) {
        this.sortBy = sortBy;
        this.ascending = ascending;
        this.date = date;
    }
}
