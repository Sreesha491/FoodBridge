package com.foodbridge.donation.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.donation.dto.DonationRequest;
import com.foodbridge.donation.dto.DonationResponse;
import com.foodbridge.donation.model.DonationStatus;
import com.foodbridge.donation.service.DonationService;
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
 * REST controller for donation management.
 * Base path: /v1/donations
 */
@RestController
@RequestMapping("/v1/donations")
@RequiredArgsConstructor
@Tag(name = "Donations", description = "Manage food donation transactions")
public class DonationController {

    private final DonationService donationService;

    @GetMapping
    @Operation(summary = "Get all donations")
    public ResponseEntity<ApiResponse<List<DonationResponse>>> getAllDonations() {
        return ResponseEntity.ok(ApiResponse.success("Donations retrieved.", donationService.getAllDonations()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get donation by ID")
    public ResponseEntity<ApiResponse<DonationResponse>> getDonationById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Donation retrieved.", donationService.getDonationById(id)));
    }

    @GetMapping("/donor/{donorId}")
    @Operation(summary = "Get donations by donor ID")
    public ResponseEntity<ApiResponse<List<DonationResponse>>> getByDonor(@PathVariable String donorId) {
        return ResponseEntity.ok(ApiResponse.success("Donations retrieved.", donationService.getDonationsByDonor(donorId)));
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Get donations by recipient ID")
    public ResponseEntity<ApiResponse<List<DonationResponse>>> getByRecipient(@PathVariable String recipientId) {
        return ResponseEntity.ok(ApiResponse.success("Donations retrieved.", donationService.getDonationsByRecipient(recipientId)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get donations by status")
    public ResponseEntity<ApiResponse<List<DonationResponse>>> getByStatus(@PathVariable DonationStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Donations retrieved.", donationService.getDonationsByStatus(status)));
    }

    @PostMapping
    @Operation(summary = "Create a new donation")
    public ResponseEntity<ApiResponse<DonationResponse>> createDonation(
            @Valid @RequestBody DonationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Donation created.", donationService.createDonation(request, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update donation details")
    public ResponseEntity<ApiResponse<DonationResponse>> updateDonation(
            @PathVariable String id,
            @Valid @RequestBody DonationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Donation updated.", donationService.updateDonation(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update donation status")
    public ResponseEntity<ApiResponse<DonationResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam DonationStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated.", donationService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a donation")
    public ResponseEntity<ApiResponse<Void>> deleteDonation(@PathVariable String id) {
        donationService.deleteDonation(id);
        return ResponseEntity.ok(ApiResponse.success("Donation deleted."));
    }
}
