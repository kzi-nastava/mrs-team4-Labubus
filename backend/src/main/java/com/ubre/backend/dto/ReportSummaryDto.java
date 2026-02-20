package com.ubre.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportSummaryDto {
    private Integer totalRides;
    private Double totalDistanceKm;
    private Double totalAmountMoney;
    private Double averageRidesPerDay;
    private Double averageDistancePerDay;
    private Double averageMoneyPerDay;
}
