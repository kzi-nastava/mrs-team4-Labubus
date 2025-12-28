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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComplaintDto> getComplaintById(@PathVariable Long id) {
        try {
            ComplaintDto complaint = ComplaintService.getComplaint(id);
            if (complaint == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(complaint, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ComplaintDto>> getDriverComplaints(@PathVariable Long driverId) {
        try {
            Collection<ComplaintDto> complaints = ComplaintService.getDriverComplaints(driverId);
            return new ResponseEntity<>(complaints, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ComplaintDto>> getUserComplaints(@PathVariable Long userId) {
        try {
            Collection<ComplaintDto> complaints = ComplaintService.getUserComplaints(userId);
            return new ResponseEntity<>(complaints, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComplaintDto> updateComplaint(
            @PathVariable Long id,
            @RequestBody ComplaintDto updateComplaintDto) {
        try {
            ComplaintDto complaint = ComplaintService.updateComplaint(id, updateComplaintDto);
            return new ResponseEntity<>(complaint, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ComplaintDto> deleteComplaint(@PathVariable Long id) {
        try {
            ComplaintDto complaint = ComplaintService.deleteComplaint(id);
            return new ResponseEntity<>(complaint, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
