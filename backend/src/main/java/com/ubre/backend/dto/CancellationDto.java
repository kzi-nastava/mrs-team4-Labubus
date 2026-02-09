package com.ubre.backend.dto;

// DTO for ride cancellation requests

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancellationDto {
    @NotBlank(message = "Cancellation reason cannot be blank")
    private String reason;

}
