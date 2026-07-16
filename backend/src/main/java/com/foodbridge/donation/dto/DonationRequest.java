package com.foodbridge.donation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

/**
 * Request DTO for creating or updating a donation.
 */
@Data
public class DonationRequest {

    @NotBlank(message = "Food item ID is required")
    private String foodItemId;

    @NotBlank(message = "Recipient ID is required")
    private String recipientId;

    private String deliveryPartnerId;
    private Instant scheduledPickupAt;
    private String pickupAddress;
    private String deliveryAddress;
    private String notes;
}
