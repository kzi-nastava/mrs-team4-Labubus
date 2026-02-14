package com.ubre.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportRequestDto {
    @NotBlank(message = "dateFrom is required")
    private String dateFrom;
    @NotBlank(message = "dateTo is required")
    private String dateTo;
    private String scope;
    private String userEmail;
}
