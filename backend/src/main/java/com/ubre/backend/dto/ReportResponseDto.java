package com.ubre.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReportResponseDto {
    private List<ReportDailyDataDto> dailyData;
    private ReportSummaryDto summary;
}
