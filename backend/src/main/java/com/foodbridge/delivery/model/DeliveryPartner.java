package com.foodbridge.delivery.model;

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

/**
 * JPA entity representing a delivery partner profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_partners")
@EntityListeners(AuditingEntityListener.class)
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Associated user account. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** Type of vehicle: "BIKE", "CAR", "VAN", "CYCLE", "WALKING". */
    private String vehicleType;

    /** Vehicle license/registration number. */
    private String vehicleNumber;

    /** Government-issued driving licence number. */
    private String licenseNumber;

    /** Current geographical area of operation. */
    private String operatingArea;

    /** Whether the partner is currently available for assignment. */
    @Builder.Default
    private boolean available = true;

    /** Average delivery rating (1–5). */
    @Builder.Default
    private Double rating = 0.0;

    /** Total number of ratings received. */
    @Builder.Default
    private Integer ratingCount = 0;

    /** Cumulative completed deliveries. */
    @Builder.Default
    private Integer completedDeliveries = 0;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
