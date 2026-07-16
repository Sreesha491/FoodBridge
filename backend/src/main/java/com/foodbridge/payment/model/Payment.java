package com.foodbridge.payment.model;

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
import com.foodbridge.order.model.Order;

/**
 * JPA entity representing a payment record on the platform.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Reference to the order this payment is for. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /** User who made the payment. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private User payer;

    /** Optional payee (e.g. delivery partner). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_id")
    private User payee;

    /** Amount in the selected currency. */
    private Double amount;

    /** Currency code e.g. "INR", "USD". */
    @Builder.Default
    private String currency = "INR";

    /** Payment method used. */
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    /** Current payment status. */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    /** External transaction reference from payment gateway. */
    private String transactionId;

    /** Gateway name (Razorpay, Stripe, etc.) or "INTERNAL". */
    private String gateway;

    /** Any failure reason, populated on FAILED status. */
    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
