package com.ubre.backend.service;

import com.ubre.backend.dto.ComplaintDto;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

public interface ComplaintService {
    ComplaintDto getComplaint(Long id) throws ResponseStatusException;
    List<ComplaintDto> getComplaint(Long userId, Long driverId) throws ResponseStatusException;
    List<ComplaintDto> getDriverComplaints(Long driverId);
    List<ComplaintDto> getUserComplaints(Long userId);
    ComplaintDto createComplaint(Long rideId, ComplaintDto complaintDto) throws ResponseStatusException;
    ComplaintDto updateComplaint(Long id, ComplaintDto complaintDto) throws ResponseStatusException;
    ComplaintDto deleteComplaint(Long id) throws ResponseStatusException;
    List<ComplaintDto> getComplaintsForRide(Long rideId);
}
