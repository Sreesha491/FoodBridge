package com.foodbridge.review.model;

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
 * JPA entity representing a review left by one user for another entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** User writing the review. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    /** ID of the entity being reviewed (user, restaurant, delivery partner). */
    private String targetId;

    /** Type of entity being reviewed: "USER", "RESTAURANT", "DELIVERY_PARTNER". */
    private String targetType;

    /** Star rating from 1 to 5. */
    private Integer rating;

    /** Free-text review comment. */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /** Optional reference to the donation or order that prompted this review. */
    private String referenceId;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
