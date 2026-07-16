package com.foodbridge.restaurant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.foodbridge.user.model.User;

/**
 * JPA entity representing a restaurant registered on FoodBridge.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurants")
@EntityListeners(AuditingEntityListener.class)
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Restaurant display name. */
    private String name;

    /** Account managing the restaurant. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    /** Physical address of the restaurant. */
    private String address;

    /** City where the restaurant is located. */
    private String city;

    /**
     * Cuisine types served e.g. "Indian", "Chinese".
     * Stored in a separate join table restaurant_cuisine_types.
     */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "restaurant_cuisine_types",
                     joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "cuisine_type")
    private List<String> cuisineTypes = new ArrayList<>();

    /** Contact phone number. */
    private String phone;

    /** Restaurant email. */
    private String email;

    /** Average rating (1–5). */
    @Builder.Default
    private Double rating = 0.0;

    /** Total number of ratings received. */
    @Builder.Default
    private Integer ratingCount = 0;

    /** Restaurant logo URL. */
    private String logoUrl;

    /** Short description / tagline. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Whether the restaurant is currently accepting orders. */
    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
