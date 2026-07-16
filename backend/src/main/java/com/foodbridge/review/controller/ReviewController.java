package com.foodbridge.review.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.review.dto.ReviewRequest;
import com.foodbridge.review.dto.ReviewResponse;
import com.foodbridge.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for review management.
 * Base path: /v1/reviews
 */
@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Submit and manage reviews for users, restaurants, and delivery partners")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get all reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved.", reviewService.getAllReviews()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Review retrieved.", reviewService.getReviewById(id)));
    }

    @GetMapping("/target/{targetId}")
    @Operation(summary = "Get reviews for a specific target entity")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByTarget(@PathVariable String targetId) {
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved.", reviewService.getReviewsByTarget(targetId)));
    }

    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Get reviews submitted by a reviewer")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByReviewer(@PathVariable String reviewerId) {
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved.", reviewService.getReviewsByReviewer(reviewerId)));
    }

    @PostMapping
    @Operation(summary = "Submit a new review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted.", reviewService.createReview(request, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable String id,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review updated.", reviewService.updateReview(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted."));
    }
}
