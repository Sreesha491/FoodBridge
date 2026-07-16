package com.foodbridge.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for submitting a review.
 */
@Data
public class ReviewRequest {

    @NotBlank(message = "Target ID is required")
    private String targetId;

    @NotBlank(message = "Target type is required (USER, RESTAURANT, DELIVERY_PARTNER)")
    private String targetType;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    private String comment;
    private String referenceId;
}
