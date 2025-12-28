package com.ubre.backend.service;

import com.ubre.backend.dto.ComplaintDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final Collection<ComplaintDto> complaints = List.of(
            new ComplaintDto(1L, 1L, 1L, "Didnt show up at requested location and time!"),
            new ComplaintDto(2L, 1L, 2L, "Drives recklessly."),
            new ComplaintDto(3L, 2L, 1L, "Didnt chose the fastest route."),
            new ComplaintDto(4L, 3L, 2L, "Very rude towards customers.")
    );

    @Override
    public ComplaintDto getComplaint(Long id) {
        Optional<ComplaintDto> targetComplaint = complaints.stream().filter( complaint -> complaint.getId() == id).findFirst();
        return targetComplaint.orElse(null);
    }

    @Override
    public ComplaintDto getComplaint(Long userId, Long driverId) {
        Optional<ComplaintDto> targetComplaint = complaints.stream().filter( complaint -> complaint.getDriverId() == driverId && complaint.getUserId() == userId).findFirst();
        return targetComplaint.orElse(null);
    }

    @Override
    public Collection<ComplaintDto> getDriverComplaints(Long driverId) {
        return complaints.stream().filter( complaint -> complaint.getDriverId() == driverId).toList();
    }

    @Override
    public Collection<ComplaintDto> getUserComplaints(Long userId) {
        return complaints.stream().filter( complaint -> complaint.getUserId() == userId).toList();
    }

    @Override
    public ComplaintDto createComplaint(ComplaintDto complaintDto) {
        complaintDto.setId(complaints.stream().mapToLong(ComplaintDto::getId).max().orElse(0) + 1);
        complaints.add(complaintDto);
        return complaintDto;
    }

    @Override
    public ComplaintDto updateComplaint(Long id, ComplaintDto complaintDto) {
        ComplaintDto complaint = getComplaint(id);
        if (complaint != null) {
            complaints.remove(complaint);
            complaintDto.setId(id);
            complaints.add(complaintDto);
            return complaintDto;
        }
        return null;
    }

    @Override
    public ComplaintDto deleteComplaint(Long id) {
        ComplaintDto complaint = getComplaint(id);
        complaints.remove(complaint);
        return complaint;
    }
}
