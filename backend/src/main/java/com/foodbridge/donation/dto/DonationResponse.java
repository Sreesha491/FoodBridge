package com.foodbridge.donation.dto;

import com.foodbridge.donation.model.DonationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for a donation record.
 */
@Data
@Builder
public class DonationResponse {

    private String id;
    private String foodItemId;
    private String donorId;
    private String recipientId;
    private String deliveryPartnerId;
    private DonationStatus status;
    private Instant scheduledPickupAt;
    private Instant pickedUpAt;
    private Instant deliveredAt;
    private String pickupAddress;
    private String deliveryAddress;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
