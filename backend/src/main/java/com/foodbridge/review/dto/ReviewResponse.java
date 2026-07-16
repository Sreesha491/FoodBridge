package com.foodbridge.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for a review record.
 */
@Data
@Builder
public class ReviewResponse {

    private String id;
    private String reviewerId;
    private String targetId;
    private String targetType;
    private Integer rating;
    private String comment;
    private String referenceId;
    private Instant createdAt;
    private Instant updatedAt;
}
