package com.ubre.backend.service;

import com.ubre.backend.dto.ReviewDto;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

public interface ReviewService {
    ReviewDto getReview(Long id) throws ResponseStatusException;
    List<ReviewDto> getDriverReviews(Long driverId);
    List<ReviewDto> getUserReviews(Long userId);
    Double getDriverAverageRating(Long driverId);
    ReviewDto createReview(Long rideId, ReviewDto reviewDto) throws ResponseStatusException;
    ReviewDto updateReview(Long id, ReviewDto reviewDto) throws ResponseStatusException;
    ReviewDto deleteReview(Long id) throws ResponseStatusException;
    ReviewDto getReviewsForRide(Long rideId);
}
