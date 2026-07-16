package com.foodbridge.review.service;

import com.foodbridge.common.exception.BadRequestException;
import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.review.dto.ReviewRequest;
import com.foodbridge.review.dto.ReviewResponse;
import com.foodbridge.review.model.Review;
import com.foodbridge.review.repository.ReviewRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer for review management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // ─── Create ───────────────────────────────────────────────────────────

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String reviewerEmail) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", reviewerEmail));

        if (reviewRepository.existsByReviewerIdAndTargetId(reviewer.getId(), request.getTargetId())) {
            throw new com.foodbridge.common.exception.BadRequestException("You have already reviewed this target");
        }
        
        Review review = Review.builder()
                .reviewer(reviewer)
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .rating(request.getRating())
                .comment(request.getComment())
                .referenceId(request.getReferenceId())
                .build();
        return toResponse(reviewRepository.save(review));
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ReviewResponse getReviewById(String id) {
        return toResponse(findById(id));
    }

    public List<ReviewResponse> getReviewsByTarget(String targetId) {
        return reviewRepository.findByTargetId(targetId).stream().map(this::toResponse).toList();
    }

    public List<ReviewResponse> getReviewsByReviewer(String reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId).stream().map(this::toResponse).toList();
    }

    // ─── Update ───────────────────────────────────────────────────────────

    @Transactional
    public ReviewResponse updateReview(String id, ReviewRequest request) {
        Review review = findById(id);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return toResponse(reviewRepository.save(review));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteReview(String id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", "id", id);
        }
        reviewRepository.deleteById(id);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private Review findById(String id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
    }

    public ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .reviewerId(r.getReviewer() != null ? r.getReviewer().getId() : null)
                .targetId(r.getTargetId())
                .targetType(r.getTargetType())
                .rating(r.getRating())
                .comment(r.getComment())
                .referenceId(r.getReferenceId())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
