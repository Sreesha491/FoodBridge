package com.foodbridge.order.model;

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
import com.foodbridge.delivery.model.DeliveryPartner;

/**
 * JPA entity representing a food order on the platform.
 * The table is named "orders" (quoted to avoid conflict with the
 * reserved SQL keyword ORDER).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_recipient", columnList = "recipient_id"),
    @Index(name = "idx_order_donor", columnList = "donor_id"),
    @Index(name = "idx_order_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** User placing the order. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    /** Donor/restaurant fulfilling the order. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private User donor;

    /** Delivery partner assigned to this order. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id")
    private DeliveryPartner deliveryPartner;

    /**
     * List of line items in the order.
     * Stored in the order_items table via @OneToMany.
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    /** Total amount (0 for free donations). */
    @Builder.Default
    private Double totalAmount = 0.0;

    /** Current status. */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    /** Delivery address. */
    private String deliveryAddress;

    /** Optional special instructions. */
    @Column(columnDefinition = "TEXT")
    private String instructions;

    /** When the order was last updated. */
    private Instant statusUpdatedAt;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
