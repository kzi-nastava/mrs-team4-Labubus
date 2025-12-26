package com.example.ubre.ui.dtos;

// this represents a single statistic item for user statistics, that is injected
// into stat card
public class StatItemDto {
    private String value;
    private String label;

    public StatItemDto(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

