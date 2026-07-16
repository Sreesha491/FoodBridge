package com.foodbridge.payment.repository;

import com.foodbridge.payment.model.Payment;
import com.foodbridge.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Payment} entities.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /** Payment for a specific order. */
    Optional<Payment> findByOrderId(String orderId);

    /** All payments made by a payer. */
    List<Payment> findByPayerId(String payerId);

    /** All payments in a given status. */
    List<Payment> findByStatus(PaymentStatus status);

    /** Payments by payer in a given status. */
    List<Payment> findByPayerIdAndStatus(String payerId, PaymentStatus status);

    /** Find by external transaction ID. */
    Optional<Payment> findByTransactionId(String transactionId);
}
