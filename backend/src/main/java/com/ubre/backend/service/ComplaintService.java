package com.ubre.backend.service;

import com.ubre.backend.dto.ComplaintDto;

import java.util.Collection;

public interface ComplaintService {
    ComplaintDto getComplaint(Long id);
    ComplaintDto getComplaint(Long userId, Long driverId);
    Collection<ComplaintDto> getDriverComplaints(Long driverId);
    Collection<ComplaintDto> getUserComplaints(Long userId);
    ComplaintDto createComplaint(ComplaintDto complaintDto);
    ComplaintDto updateComplaint(Long id, ComplaintDto complaintDto);
    ComplaintDto deleteComplaint(Long id);
}
