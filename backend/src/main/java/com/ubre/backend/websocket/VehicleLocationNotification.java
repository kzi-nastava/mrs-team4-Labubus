package com.ubre.backend.websocket;

import com.ubre.backend.dto.VehicleIndicatorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocationNotification {
    private String status;
    private List<VehicleIndicatorDto> indicators;
}
