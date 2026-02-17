package com.example.ubre.ui.dtos;

import java.util.List;

public class ReportsResponseDto {
    private List<DailyReportEntryDto> dailyData;
    private ReportSummaryDto summary;

    public ReportsResponseDto() {}

    public List<DailyReportEntryDto> getDailyData() {
        return dailyData;
    }

    public void setDailyData(List<DailyReportEntryDto> dailyData) {
        this.dailyData = dailyData;
    }

    public ReportSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(ReportSummaryDto summary) {
        this.summary = summary;
    }
}
