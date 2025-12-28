package com.ubre.backend.controller;

import com.ubre.backend.dto.ComplaintDto;
import com.ubre.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    @Autowired
    private ComplaintService ComplaintService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComplaintDto> createComplaint(
            @RequestBody ComplaintDto createComplaintDto) {
        try {
            ComplaintDto Complaint = ComplaintService.createComplaint(createComplaintDto);
            return new ResponseEntity<>(Complaint, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComplaintDto> getComplaintById(@PathVariable Long id) {
        try {
            ComplaintDto Complaint = ComplaintService.getComplaint(id);
            return new ResponseEntity<>(Complaint, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ComplaintDto>> getDriverComplaints(@PathVariable Long driverId) {
        try {
            Collection<ComplaintDto> complaints = ComplaintService.getDriverComplaints(driverId);
            return new ResponseEntity<>(complaints, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ComplaintDto>> getUserComplaints(@PathVariable Long userId) {
        try {
            Collection<ComplaintDto> complaints = ComplaintService.getUserComplaints(userId);
            return new ResponseEntity<>(complaints, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComplaintDto> updateComplaint(
            @PathVariable Long id,
            @RequestBody ComplaintDto updateComplaintDto) {
        try {
            ComplaintDto Complaint = ComplaintService.updateComplaint(id, updateComplaintDto);
            return new ResponseEntity<>(Complaint, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        try {
            ComplaintService.deleteComplaint(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
