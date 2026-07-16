package com.foodbridge.donation.repository;

import com.foodbridge.donation.model.Donation;
import com.foodbridge.donation.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Donation} entities.
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation, String> {

    /** All donations created by a donor. */
    List<Donation> findByDonorId(String donorId);

    /** All donations received by a recipient. */
    List<Donation> findByRecipientId(String recipientId);

    /** All donations in a given status. */
    List<Donation> findByStatus(DonationStatus status);

    /** Donations by food item. */
    List<Donation> findByFoodItemId(String foodItemId);

    /** Donations assigned to a delivery partner. */
    List<Donation> findByDeliveryPartnerId(String deliveryPartnerId);

    /** Donor's donations in a specific status. */
    List<Donation> findByDonorIdAndStatus(String donorId, DonationStatus status);
}
