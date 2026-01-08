package com.ubre.backend.dto;

// this represents a single statistic item for user statistics, that is injected
// into stat card

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatItemDto {
    private String value;
    private String label;

    public StatItemDto(String value, String label) {
        this.value = value;
        this.label = label;
    }
}

