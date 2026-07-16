package com.foodbridge.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import com.foodbridge.user.model.User;

/**
 * JPA entity representing an in-app notification for a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** User receiving the notification. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** Short notification title. */
    private String title;

    /** Full notification body. */
    @Column(columnDefinition = "TEXT")
    private String message;

    /** Category of this notification. */
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    /** Whether the user has read this notification. */
    @Builder.Default
    @Column(name = "is_read")
    private boolean read = false;

    /** Optional link to the related entity (donation ID, order ID, etc.). */
    private String referenceId;

    /** Optional reference type string ("DONATION", "ORDER", "PAYMENT"). */
    private String referenceType;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;
}
