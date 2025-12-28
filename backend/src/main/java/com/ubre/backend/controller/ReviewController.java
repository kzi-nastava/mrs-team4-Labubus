package com.ubre.backend.controller;

import com.ubre.backend.dto.ReviewDto;
import com.ubre.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService ReviewService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDto> createReview(
            @RequestBody ReviewDto createReviewDto) {
        try {
            ReviewDto review = ReviewService.createReview(createReviewDto);
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {
        try {
            ReviewDto review = ReviewService.getReview(id);
            if (review == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDto>> getDriverReviews(@PathVariable Long driverId) {
        try {
            Collection<ReviewDto> reviews = ReviewService.getDriverReviews(driverId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDto>> getUserReviews(@PathVariable Long userId) {
        try {
            Collection<ReviewDto> reviews = ReviewService.getDriverReviews(userId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewDto updateReviewDto) {
        try {
            ReviewDto Review = ReviewService.updateReview(id, updateReviewDto);
            return new ResponseEntity<>(Review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        try {
            ReviewService.deleteReview(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
