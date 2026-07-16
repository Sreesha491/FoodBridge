package com.foodbridge.delivery.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.delivery.dto.DeliveryPartnerRequest;
import com.foodbridge.delivery.dto.DeliveryPartnerResponse;
import com.foodbridge.delivery.service.DeliveryPartnerService;
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
 * REST controller for delivery partner management.
 * Base path: /v1/delivery-partners
 */
@RestController
@RequestMapping("/v1/delivery-partners")
@RequiredArgsConstructor
@Tag(name = "Delivery Partners", description = "Manage delivery partner profiles and availability")
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    @GetMapping
    @Operation(summary = "Get all delivery partners")
    public ResponseEntity<ApiResponse<List<DeliveryPartnerResponse>>> getAllPartners() {
        return ResponseEntity.ok(ApiResponse.success("Delivery partners retrieved.", deliveryPartnerService.getAllPartners()));
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available delivery partners")
    public ResponseEntity<ApiResponse<List<DeliveryPartnerResponse>>> getAvailablePartners() {
        return ResponseEntity.ok(ApiResponse.success("Available partners retrieved.", deliveryPartnerService.getAvailablePartners()));
    }

    @GetMapping("/available/area/{area}")
    @Operation(summary = "Get available delivery partners by operating area")
    public ResponseEntity<ApiResponse<List<DeliveryPartnerResponse>>> getAvailableByArea(@PathVariable String area) {
        return ResponseEntity.ok(ApiResponse.success("Partners retrieved.", deliveryPartnerService.getAvailablePartnersByArea(area)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery partner by ID")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponse>> getPartnerById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Partner retrieved.", deliveryPartnerService.getPartnerById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get delivery partner by user ID")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponse>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("Partner retrieved.", deliveryPartnerService.getPartnerByUserId(userId)));
    }

    @PostMapping
    @Operation(summary = "Register as a delivery partner")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponse>> createProfile(
            @Valid @RequestBody DeliveryPartnerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created.", deliveryPartnerService.createProfile(request, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update delivery partner profile")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponse>> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody DeliveryPartnerRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated.", deliveryPartnerService.updateProfile(id, request)));
    }

    @PatchMapping("/{id}/toggle-availability")
    @Operation(summary = "Toggle delivery partner availability")
    public ResponseEntity<ApiResponse<DeliveryPartnerResponse>> toggleAvailability(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Availability toggled.", deliveryPartnerService.toggleAvailability(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a delivery partner profile")
    public ResponseEntity<ApiResponse<Void>> deletePartner(@PathVariable String id) {
        deliveryPartnerService.deletePartner(id);
        return ResponseEntity.ok(ApiResponse.success("Partner deleted."));
    }
}
