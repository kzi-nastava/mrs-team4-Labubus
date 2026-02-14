package com.ubre.backend.service;

import com.ubre.backend.dto.ReportRequestDto;
import com.ubre.backend.dto.ReportResponseDto;

public interface ReportService {
    ReportResponseDto generateReport(ReportRequestDto request);
}
