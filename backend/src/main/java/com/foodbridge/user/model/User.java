package com.foodbridge.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * JPA entity representing a FoodBridge platform user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_role", columnList = "role")
})
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Full name of the user. */
    @Column(nullable = false, length = 100)
    private String name;

    /** Unique email used for login. */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /** BCrypt-hashed password. Never exposed in API responses. */
    @Column(nullable = false, length = 255)
    private String password;

    /** Platform role governing endpoint access. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    /** Contact phone number. */
    private String phone;

    /** Physical address (city, state, etc.). */
    private String address;

    /** Profile picture URL. */
    private String profileImageUrl;

    /** Whether this account is active and can log in. */
    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
