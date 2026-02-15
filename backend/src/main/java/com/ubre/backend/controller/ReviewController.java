package com.ubre.backend.controller;

import com.ubre.backend.dto.ReviewDto;
import com.ubre.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService ReviewService;

    @PostMapping(value = "/ride/{rideId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#createReviewDto.getUserId() == @securityUtil.currentUserId()")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable Long rideId,
            @Valid @RequestBody ReviewDto createReviewDto) {
        ReviewDto review = ReviewService.createReview(rideId, createReviewDto);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {
        ReviewDto review = ReviewService.getReview(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReviewDto>> getDriverReviews(@PathVariable Long driverId) {
        List<ReviewDto> reviews = ReviewService.getDriverReviews(driverId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReviewDto>> getUserReviews(@PathVariable Long userId) {
        List<ReviewDto> reviews = ReviewService.getUserReviews(userId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#updateReviewDto.getUserId() == @securityUtil.currentUserId()")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewDto updateReviewDto) {
        ReviewDto Review = ReviewService.updateReview(id, updateReviewDto);
        return new ResponseEntity<>(Review, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ReviewDto> deleteReview(@PathVariable Long id) {
        ReviewDto review = ReviewService.deleteReview(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }
    @GetMapping("/ride/{rideId}")
    public ResponseEntity<ReviewDto> getReviewsForRide(@PathVariable Long rideId) {
        ReviewDto review = ReviewService.getReviewsForRide(rideId);
        return ResponseEntity.ok(review);
    }

}
