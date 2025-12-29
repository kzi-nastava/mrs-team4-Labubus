package com.ubre.backend.service;

import com.ubre.backend.dto.ReviewDto;

import java.util.Collection;

public interface ReviewService {
    ReviewDto getReview(Long id);
    Collection<ReviewDto> getDriverReviews(Long driverId);
    Collection<ReviewDto> getUserReviews(Long userId);
    Double getDriverAverageRating(Long driverId);
    ReviewDto createReview(ReviewDto reviewDto);
    ReviewDto updateReview(Long id, ReviewDto reviewDto);
    ReviewDto deleteReview(Long id);
}
