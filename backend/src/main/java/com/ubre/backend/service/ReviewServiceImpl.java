package com.ubre.backend.service;

import com.ubre.backend.dto.ReviewDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    Collection<ReviewDto> reviews = List.of(
            new ReviewDto(1L, 1L, 1L, 5, "Fair and reliable service."),
            new ReviewDto(2L, 2L, 1L, 4, "Does not have AC."),
            new ReviewDto(3L, 2L, 2L, 3, "Couldnt stop talking during the ride."),
            new ReviewDto(4L, 3L, 2L, 4, "Vehicle was very loud.")
    );


    @Override
    public ReviewDto getReview(Long id) {
        Optional<ReviewDto> targetReview = reviews.stream().filter(review -> review.getId() == id).findFirst();
        return targetReview.orElse(null);
    }

    @Override
    public Collection<ReviewDto> getDriverReviews(Long driverId) {
        return reviews.stream().filter(review -> review.getDriverId() == driverId).toList();
    }

    @Override
    public Collection<ReviewDto> getUserReviews(Long userId) {
        return reviews.stream().filter(review -> review.getUserId() == userId).toList();
    }

    @Override
    public Double getDriverAverageRating(Long driverId) {
        return getDriverReviews(driverId).stream().mapToInt(ReviewDto::getRating).average().orElse(0);
    }

    @Override
    public ReviewDto createReview(ReviewDto reviewDto) {
        reviewDto.setId(reviews.stream().mapToLong(ReviewDto::getId).max().orElse(0) + 1);
        reviews.add(reviewDto);
        return reviewDto;
    }

    @Override
    public ReviewDto updateReview(Long id, ReviewDto reviewDto) {
        ReviewDto review = getReview(id);
        if (review != null) {
            reviewDto.setId(id);
            reviews.remove(review);
            reviews.add(reviewDto);
            return reviewDto;
        }
        return null;
    }

    @Override
    public ReviewDto deleteReview(Long id) {
        ReviewDto review = getReview(id);
        reviews.remove(review);
        return review;
    }
}
