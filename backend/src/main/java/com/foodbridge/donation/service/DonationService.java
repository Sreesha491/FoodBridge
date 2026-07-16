package com.foodbridge.donation.service;

import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.delivery.model.DeliveryPartner;
import com.foodbridge.delivery.repository.DeliveryPartnerRepository;
import com.foodbridge.donation.dto.DonationRequest;
import com.foodbridge.donation.dto.DonationResponse;
import com.foodbridge.donation.model.Donation;
import com.foodbridge.donation.model.DonationStatus;
import com.foodbridge.donation.repository.DonationRepository;
import com.foodbridge.food.model.FoodItem;
import com.foodbridge.food.repository.FoodItemRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Business logic layer for donation management.
 *
 * <p>Manages the full lifecycle of food donations:
 * PENDING → ACCEPTED → PICKED_UP → DELIVERED (or CANCELLED).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    // ─── Create ───────────────────────────────────────────────────────────

    /**
     * Creates a donation. The donor email is from the authenticated Spring Security principal
     * and is resolved to an actual User entity.
     *
     * @param request   donation details
     * @param donorEmail authenticated user's email
     * @return created donation DTO
     */
    @Transactional
    public DonationResponse createDonation(DonationRequest request, String donorEmail) {
        User donor = userRepository.findByEmail(donorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", donorEmail));

        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", request.getFoodItemId()));

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRecipientId()));

        DeliveryPartner deliveryPartner = null;
        if (request.getDeliveryPartnerId() != null && !request.getDeliveryPartnerId().isBlank()) {
            deliveryPartner = deliveryPartnerRepository.findById(request.getDeliveryPartnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeliveryPartner", "id", request.getDeliveryPartnerId()));
        }

        Donation donation = Donation.builder()
                .foodItem(foodItem)
                .donor(donor)
                .recipient(recipient)
                .deliveryPartner(deliveryPartner)
                .status(DonationStatus.PENDING)
                .scheduledPickupAt(request.getScheduledPickupAt())
                .pickupAddress(request.getPickupAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .notes(request.getNotes())
                .build();

        Donation saved = donationRepository.save(donation);
        log.info("Donation [{}] created by donor [{}] for food item [{}]",
                saved.getId(), donorEmail, foodItem.getId());
        return toResponse(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<DonationResponse> getAllDonations() {
        return donationRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DonationResponse getDonationById(String id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<DonationResponse> getDonationsByDonor(String donorId) {
        return donationRepository.findByDonorId(donorId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DonationResponse> getDonationsByRecipient(String recipientId) {
        return donationRepository.findByRecipientId(recipientId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DonationResponse> getDonationsByStatus(DonationStatus status) {
        return donationRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    // ─── Update ───────────────────────────────────────────────────────────

    @Transactional
    public DonationResponse updateDonation(String id, DonationRequest request) {
        Donation donation = findById(id);

        DeliveryPartner deliveryPartner = null;
        if (request.getDeliveryPartnerId() != null && !request.getDeliveryPartnerId().isBlank()) {
            deliveryPartner = deliveryPartnerRepository.findById(request.getDeliveryPartnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeliveryPartner", "id", request.getDeliveryPartnerId()));
        }

        donation.setDeliveryPartner(deliveryPartner);
        donation.setScheduledPickupAt(request.getScheduledPickupAt());
        donation.setPickupAddress(request.getPickupAddress());
        donation.setDeliveryAddress(request.getDeliveryAddress());
        donation.setNotes(request.getNotes());

        log.debug("Donation [{}] updated", id);
        return toResponse(donationRepository.save(donation));
    }

    @Transactional
    public DonationResponse updateStatus(String id, DonationStatus status) {
        Donation donation = findById(id);
        donation.setStatus(status);

        if (status == DonationStatus.PICKED_UP) {
            donation.setPickedUpAt(Instant.now());
        } else if (status == DonationStatus.DELIVERED) {
            donation.setDeliveredAt(Instant.now());
        }

        log.info("Donation [{}] status updated to [{}]", id, status);
        return toResponse(donationRepository.save(donation));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteDonation(String id) {
        if (!donationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Donation", "id", id);
        }
        donationRepository.deleteById(id);
        log.info("Donation [{}] deleted", id);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private Donation findById(String id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", "id", id));
    }

    public DonationResponse toResponse(Donation d) {
        return DonationResponse.builder()
                .id(d.getId())
                .foodItemId(d.getFoodItem() != null ? d.getFoodItem().getId() : null)
                .donorId(d.getDonor() != null ? d.getDonor().getId() : null)
                .recipientId(d.getRecipient() != null ? d.getRecipient().getId() : null)
                .deliveryPartnerId(d.getDeliveryPartner() != null ? d.getDeliveryPartner().getId() : null)
                .status(d.getStatus())
                .scheduledPickupAt(d.getScheduledPickupAt())
                .pickedUpAt(d.getPickedUpAt())
                .deliveredAt(d.getDeliveredAt())
                .pickupAddress(d.getPickupAddress())
                .deliveryAddress(d.getDeliveryAddress())
                .notes(d.getNotes())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
