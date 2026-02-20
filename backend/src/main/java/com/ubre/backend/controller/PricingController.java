package com.ubre.backend.controller;

import com.ubre.backend.dto.VehicleTypePricingDto;
import com.ubre.backend.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@CrossOrigin(origins = "*")
public class PricingController {

    @Autowired
    private PricingService pricingService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleTypePricingDto> getPricing() {
        VehicleTypePricingDto pricing = pricingService.getPricing();
        return ResponseEntity.status(HttpStatus.OK).body(pricing);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleTypePricingDto> updatePricing(@RequestBody VehicleTypePricingDto dto) {
        VehicleTypePricingDto updated = pricingService.updatePricing(dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
}
