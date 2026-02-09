package com.ubre.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

// DTO for querying rides with sorting and filtering options

@Getter
@Setter
@NoArgsConstructor
public class RideQueryDto {
    private String sortBy;
    private Boolean ascending;
    private LocalDateTime date; // very questionable fix later or convert to string

    public RideQueryDto(Long userId, String sortBy, Boolean ascending, LocalDateTime date) {
        this.sortBy = sortBy;
        this.ascending = ascending;
        this.date = date;
    }
}
