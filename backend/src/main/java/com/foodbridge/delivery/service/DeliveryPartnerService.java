package com.foodbridge.delivery.service;

import com.foodbridge.common.exception.BadRequestException;
import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.delivery.dto.DeliveryPartnerRequest;
import com.foodbridge.delivery.dto.DeliveryPartnerResponse;
import com.foodbridge.delivery.model.DeliveryPartner;
import com.foodbridge.delivery.repository.DeliveryPartnerRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Business logic layer for delivery partner management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final UserRepository userRepository;

    // ─── Create ───────────────────────────────────────────────────────────

    @Transactional
    public DeliveryPartnerResponse createProfile(DeliveryPartnerRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        if (deliveryPartnerRepository.findByUserId(user.getId()).isPresent()) {
            throw new BadRequestException("User is already registered as a delivery partner.");
        }
        DeliveryPartner partner = DeliveryPartner.builder()
                .user(user)
                .vehicleType(request.getVehicleType())
                .vehicleNumber(request.getVehicleNumber())
                .licenseNumber(request.getLicenseNumber())
                .operatingArea(request.getOperatingArea())
                .available(true)
                .build();
        DeliveryPartner saved = deliveryPartnerRepository.save(partner);
        log.info("Delivery partner profile created for user [{}]", userEmail);
        return toResponse(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponse> getAllPartners() {
        return deliveryPartnerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DeliveryPartnerResponse getPartnerById(String id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public DeliveryPartnerResponse getPartnerByUserId(String userId) {
        return deliveryPartnerRepository.findByUserId(userId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryPartner", "userId", userId));
    }

    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponse> getAvailablePartners() {
        return deliveryPartnerRepository.findByAvailableTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponse> getAvailablePartnersByArea(String area) {
        return deliveryPartnerRepository.findByAvailableTrueAndOperatingArea(area)
                .stream().map(this::toResponse).toList();
    }

    // ─── Update ───────────────────────────────────────────────────────────

    @Transactional
    public DeliveryPartnerResponse updateProfile(String id, DeliveryPartnerRequest request) {
        DeliveryPartner partner = findById(id);
        partner.setVehicleType(request.getVehicleType());
        partner.setVehicleNumber(request.getVehicleNumber());
        partner.setLicenseNumber(request.getLicenseNumber());
        partner.setOperatingArea(request.getOperatingArea());
        return toResponse(deliveryPartnerRepository.save(partner));
    }

    @Transactional
    public DeliveryPartnerResponse toggleAvailability(String id) {
        DeliveryPartner partner = findById(id);
        partner.setAvailable(!partner.isAvailable());
        return toResponse(deliveryPartnerRepository.save(partner));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deletePartner(String id) {
        if (!deliveryPartnerRepository.existsById(id)) {
            throw new ResourceNotFoundException("DeliveryPartner", "id", id);
        }
        deliveryPartnerRepository.deleteById(id);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private DeliveryPartner findById(String id) {
        return deliveryPartnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryPartner", "id", id));
    }

    public DeliveryPartnerResponse toResponse(DeliveryPartner dp) {
        return DeliveryPartnerResponse.builder()
                .id(dp.getId())
                .userId(dp.getUser() != null ? dp.getUser().getId() : null)
                .vehicleType(dp.getVehicleType())
                .vehicleNumber(dp.getVehicleNumber())
                .licenseNumber(dp.getLicenseNumber())
                .operatingArea(dp.getOperatingArea())
                .available(dp.isAvailable())
                .rating(dp.getRating())
                .ratingCount(dp.getRatingCount())
                .completedDeliveries(dp.getCompletedDeliveries())
                .createdAt(dp.getCreatedAt())
                .updatedAt(dp.getUpdatedAt())
                .build();
    }
}
