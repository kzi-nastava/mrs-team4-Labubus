package com.ubre.backend.controller;

import com.ubre.backend.dto.ComplaintDto;
import com.ubre.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    @Autowired
    private ComplaintService ComplaintService;

    @PostMapping(value = "/ride/{rideId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#createComplaintDto.getUserId() == @securityUtil.currentUserId()")
    public ResponseEntity<ComplaintDto> createComplaint(
            @PathVariable Long rideId,
            @RequestBody ComplaintDto createComplaintDto) {
        ComplaintDto Complaint = ComplaintService.createComplaint(rideId, createComplaintDto);
        return new ResponseEntity<>(Complaint, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComplaintDto> getComplaintById(@PathVariable Long id) {
        ComplaintDto complaint = ComplaintService.getComplaint(id);
        return new ResponseEntity<>(complaint, HttpStatus.OK);

    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComplaintDto>> getDriverComplaints(@PathVariable Long driverId) {
        List<ComplaintDto> complaints = ComplaintService.getDriverComplaints(driverId);
        return new ResponseEntity<>(complaints, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComplaintDto>> getUserComplaints(@PathVariable Long userId) {
        List<ComplaintDto> complaints = ComplaintService.getUserComplaints(userId);
        return new ResponseEntity<>(complaints, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#updateComplaint.getUserId() == @securityUtil.currentUserId()")
    public ResponseEntity<ComplaintDto> updateComplaint(
            @PathVariable Long id,
            @RequestBody ComplaintDto updateComplaintDto) {
        ComplaintDto complaint = ComplaintService.updateComplaint(id, updateComplaintDto);
        return new ResponseEntity<>(complaint, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ComplaintDto> deleteComplaint(@PathVariable Long id) {
        ComplaintDto complaint = ComplaintService.deleteComplaint(id);
        return new ResponseEntity<>(complaint, HttpStatus.OK);
    }
}
