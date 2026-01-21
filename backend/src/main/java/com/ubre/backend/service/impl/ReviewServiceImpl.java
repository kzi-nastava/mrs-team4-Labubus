package com.ubre.backend.service.impl;

import com.ubre.backend.dto.ReviewDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Review;
import com.ubre.backend.model.Ride;
import com.ubre.backend.model.User;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.ReviewRepository;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RideRepository rideRepository;

//    Collection<ReviewDto> reviews = List.of(
//            new ReviewDto(1L, 1L, 1L, 5, "Fair and reliable service."),
//            new ReviewDto(2L, 2L, 1L, 4, "Does not have AC."),
//            new ReviewDto(3L, 2L, 2L, 3, "Couldnt stop talking during the ride."),
//            new ReviewDto(4L, 3L, 2L, 4, "Vehicle was very loud.")
//    );


    @Override
    public ReviewDto getReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");

        return new ReviewDto(review.get());
    }

    @Override
    public Collection<ReviewDto> getDriverReviews(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");

        List<Review> driverReviews = reviewRepository.findByDriver(driver.get());
        return driverReviews.stream().map(ReviewDto::new).toList();
    }

    @Override
    public Collection<ReviewDto> getUserReviews(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty() /*|| user.get().getRole() != Role.REGISTERED_USER*/)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<Review> userReviews = reviewRepository.findByUser(user.get());
        return userReviews.stream().map(ReviewDto::new).toList();
    }

    @Override
    public Double getDriverAverageRating(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");

        return reviewRepository.getAverageDriverRating(driver.get());
    }

    @Override
    public ReviewDto createReview(Long rideId, ReviewDto reviewDto) {
        Optional<User> user = userRepository.findById(reviewDto.getUserId());
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");

        if (ChronoUnit.DAYS.between(ride.get().getEndTime(), LocalDateTime.now()) > 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review may only be made within 3 days of ride completion");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (!ride.get().getCreator().getId().equals(reviewDto.getUserId()) || !jwtUser.getId().equals(reviewDto.getUserId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator may review the ride");

        Optional<Review> review = reviewRepository.findByRide(ride.get());
        if (review.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review already exists");

        Review newReview = new Review(reviewDto);
        newReview.setDriver(ride.get().getDriver());
        newReview.setUser(user.get());
        newReview.setRide(ride.get());
        return new ReviewDto(reviewRepository.save(newReview));
    }

    @Override
    public ReviewDto updateReview(Long id, ReviewDto reviewDto) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (!jwtUser.getId().equals(review.get().getUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator may review the ride");

        Review updatedReview = review.get();
        updatedReview.setText(reviewDto.getText());
        updatedReview.setRating(reviewDto.getRating());
        return  new ReviewDto(reviewRepository.save(updatedReview));
    }

    @Override
    public ReviewDto deleteReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jwtUser = (User) auth.getPrincipal();
        if (!review.get().getUser().getId().equals(jwtUser.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the author of the review can delete it");

        reviewRepository.delete(review.get());
        return new ReviewDto(review.get());
    }
}
