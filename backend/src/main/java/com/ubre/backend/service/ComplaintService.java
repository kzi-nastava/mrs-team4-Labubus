package com.ubre.backend.service;

import com.ubre.backend.dto.ComplaintDto;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

public interface ComplaintService {
    ComplaintDto getComplaint(Long id) throws ResponseStatusException;
    Collection<ComplaintDto> getComplaint(Long userId, Long driverId) throws ResponseStatusException;
    Collection<ComplaintDto> getDriverComplaints(Long driverId);
    Collection<ComplaintDto> getUserComplaints(Long userId);
    ComplaintDto createComplaint(Long rideId, ComplaintDto complaintDto) throws ResponseStatusException;
    ComplaintDto updateComplaint(Long id, ComplaintDto complaintDto) throws ResponseStatusException;
    ComplaintDto deleteComplaint(Long id) throws ResponseStatusException;
}
