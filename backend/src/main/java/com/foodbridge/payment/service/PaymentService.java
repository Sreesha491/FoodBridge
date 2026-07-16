package com.foodbridge.payment.service;

import com.foodbridge.common.exception.BadRequestException;
import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.order.model.Order;
import com.foodbridge.order.repository.OrderRepository;
import com.foodbridge.payment.dto.PaymentRequest;
import com.foodbridge.payment.dto.PaymentResponse;
import com.foodbridge.payment.model.Payment;
import com.foodbridge.payment.model.PaymentStatus;
import com.foodbridge.payment.repository.PaymentRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer for payment management.
 *
 * <p>Handles payment lifecycle for food orders on the FoodBridge platform.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // ─── Create (Initiate) ────────────────────────────────────────────────

    /**
     * Initiates a payment for an order. Prevents duplicate non-failed payments.
     *
     * @param request    payment details
     * @param payerEmail authenticated user's email
     * @return created payment DTO
     */
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request, String payerEmail) {
        User payer = userRepository.findByEmail(payerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", payerEmail));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        // Prevent duplicate payments for the same order
        paymentRepository.findByOrderId(request.getOrderId()).ifPresent(p -> {
            if (p.getStatus() != PaymentStatus.FAILED) {
                throw new BadRequestException(
                        "A payment already exists for order: " + request.getOrderId());
            }
        });

        Payment payment = Payment.builder()
                .order(order)
                .payer(payer)
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .transactionId(request.getTransactionId())
                .gateway(request.getGateway())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment [{}] initiated for order [{}] by user [{}]",
                saved.getId(), order.getId(), payerEmail);
        return toResponse(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByPayer(String payerId) {
        return paymentRepository.findByPayerId(payerId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    // ─── Update Status ────────────────────────────────────────────────────

    @Transactional
    public PaymentResponse updateStatus(String id, PaymentStatus status, String failureReason) {
        Payment payment = findById(id);
        payment.setStatus(status);
        if (failureReason != null) {
            payment.setFailureReason(failureReason);
        }
        log.info("Payment [{}] status changed to [{}]", id, status);
        return toResponse(paymentRepository.save(payment));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deletePayment(String id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment", "id", id);
        }
        paymentRepository.deleteById(id);
        log.info("Payment [{}] deleted", id);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private Payment findById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    public PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrder() != null ? p.getOrder().getId() : null)
                .payerId(p.getPayer() != null ? p.getPayer().getId() : null)
                .payeeId(p.getPayee() != null ? p.getPayee().getId() : null)
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .method(p.getMethod())
                .status(p.getStatus())
                .transactionId(p.getTransactionId())
                .gateway(p.getGateway())
                .failureReason(p.getFailureReason())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
