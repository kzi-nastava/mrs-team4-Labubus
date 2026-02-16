package com.ubre.backend.service.impl;

import com.ubre.backend.dto.ComplaintDto;
import com.ubre.backend.model.Complaint;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Ride;
import com.ubre.backend.model.User;
import com.ubre.backend.repository.ComplaintRepository;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    @Autowired
    private ComplaintRepository complaintRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RideRepository rideRepository;

    @Override
    public ComplaintDto getComplaint(Long id) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        if (complaint.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found");

        return new ComplaintDto(complaint.get());
    }

    @Override
    public List<ComplaintDto> getComplaint(Long userId, Long driverId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");

        List<Complaint> complaints = complaintRepository.findByUserAndDriver(user.get(), driver.get());
        return complaints.stream().map(ComplaintDto::new).toList();
    }

    @Override
    public List<ComplaintDto> getDriverComplaints(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");

        List<Complaint> complaints = complaintRepository.findByDriver(driver.get());
        return complaints.stream().map(ComplaintDto::new).toList();
    }

    @Override
    public List<ComplaintDto> getUserComplaints(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<Complaint> complaints = complaintRepository.findByUser(user.get());
        return complaints.stream().map(ComplaintDto::new).toList();
    }

    @Override
    public ComplaintDto createComplaint(Long rideId, ComplaintDto complaintDto) {
        Optional<User> user = userRepository.findById(complaintDto.getUserId());
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (ride.get().getPassengers().stream().noneMatch(passenger -> passenger.getId().equals(complaintDto.getUserId())) || ride.get().getPassengers().stream().noneMatch(passenger -> passenger.getId().equals(jwtUser.getId())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the customer may submit a complaint");

        complaintDto.setId(null);
        Complaint complaint = new Complaint(complaintDto);
        complaint.setUser(user.get());
        complaint.setDriver(ride.get().getDriver());
        complaint.setRide(ride.get());
        return new ComplaintDto(complaintRepository.save(complaint));
    }

    @Override
    public ComplaintDto updateComplaint(Long id, ComplaintDto complaintDto) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        if (complaint.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (!complaint.get().getUser().getId().equals(jwtUser.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the customer may edit their complaint");

        Complaint updatedComplaint = complaint.get();
        updatedComplaint.setText(complaintDto.getText());
        return new ComplaintDto(complaintRepository.save(updatedComplaint));
    }

    @Override
    public ComplaintDto deleteComplaint(Long id) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        if (complaint.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (!complaint.get().getUser().getId().equals(jwtUser.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You may only delete your complaints");

        complaintRepository.delete(complaint.get());
        return new ComplaintDto(complaint.get());
    }

    @Override
    public List<ComplaintDto> getComplaintsForRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        List<Complaint> complaints = ride.getComplaints();

        return complaints.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ComplaintDto mapToDto(Complaint complaint) {
        ComplaintDto dto = new ComplaintDto();
        dto.setId(complaint.getId());
        dto.setDriverId(complaint.getRide().getDriver().getId());
        dto.setUserId(complaint.getUser().getId());
        dto.setText(complaint.getText());
        return dto;
    }

}
