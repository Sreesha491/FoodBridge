package com.foodbridge.food.model;

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
import com.foodbridge.restaurant.model.Restaurant;

/**
 * JPA entity representing a food item listed for donation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "food_items", indexes = {
    @Index(name = "idx_food_donor", columnList = "donor_id"),
    @Index(name = "idx_food_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Display name of the food item. */
    private String name;

    /** Detailed description (ingredients, allergens, preparation method). */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Category e.g. "Cooked Meal", "Raw Produce", "Packaged Food". */
    private String category;

    /** Numeric quantity available. */
    private Double quantity;

    /** Unit of measure e.g. "kg", "litres", "pieces". */
    private String unit;

    /** Date/time after which food should not be consumed. */
    private Instant expiryDate;

    /** ID of the donor (User) who listed this item. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private User donor;

    /** Optional restaurant ID if listed by a restaurant account. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    /** Current lifecycle status. */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private FoodStatus status = FoodStatus.AVAILABLE;

    /** URL of the food image. */
    private String imageUrl;

    /** Pickup address if different from donor's registered address. */
    private String pickupAddress;

    /** Any special handling instructions. */
    @Column(columnDefinition = "TEXT")
    private String specialInstructions;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
