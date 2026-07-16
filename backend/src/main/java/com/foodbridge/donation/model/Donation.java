package com.foodbridge.donation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import com.foodbridge.user.model.User;
import com.foodbridge.food.model.FoodItem;
import com.foodbridge.delivery.model.DeliveryPartner;

/**
 * JPA entity representing a food donation transaction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "donations")
@EntityListeners(AuditingEntityListener.class)
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Food item being donated. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    /** Donor (User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private User donor;

    /** Recipient (NGO or individual User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    /** Delivery partner, if assigned. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id")
    private DeliveryPartner deliveryPartner;

    /** Current status of the donation. */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.PENDING;

    /** When the pickup is scheduled. */
    private Instant scheduledPickupAt;

    /** When the donation was picked up. */
    private Instant pickedUpAt;

    /** When the donation was delivered. */
    private Instant deliveredAt;

    /** Pickup address. */
    private String pickupAddress;

    /** Delivery address. */
    private String deliveryAddress;

    /** Any notes from donor or recipient. */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
