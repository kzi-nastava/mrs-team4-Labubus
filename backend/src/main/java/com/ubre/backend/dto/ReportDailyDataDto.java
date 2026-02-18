package com.ubre.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportDailyDataDto {
    private String date; // YYYY-MM-DD
    private Integer rideCount;
    private Double distanceKm;
    private Double amountMoney;
}
