package com.foodbridge.order.service;

import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.delivery.model.DeliveryPartner;
import com.foodbridge.delivery.repository.DeliveryPartnerRepository;
import com.foodbridge.food.model.FoodItem;
import com.foodbridge.food.repository.FoodItemRepository;
import com.foodbridge.order.dto.OrderItemDto;
import com.foodbridge.order.dto.OrderRequest;
import com.foodbridge.order.dto.OrderResponse;
import com.foodbridge.order.model.Order;
import com.foodbridge.order.model.OrderItem;
import com.foodbridge.order.model.OrderStatus;
import com.foodbridge.order.repository.OrderRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic layer for order management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    // ─── Create ───────────────────────────────────────────────────────────

    @Transactional
    public OrderResponse createOrder(OrderRequest request, String recipientEmail) {
        User recipient = userRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", recipientEmail));
        User donor = userRepository.findById(request.getDonorId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getDonorId()));

        List<OrderItem> orderItems = request.getItems().stream().map(dto -> {
            FoodItem foodItem = foodItemRepository.findById(dto.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", dto.getFoodItemId()));
            OrderItem item = new OrderItem();
            item.setFoodItem(foodItem);
            item.setFoodItemName(dto.getFoodItemName() != null ? dto.getFoodItemName() : foodItem.getName());
            item.setQuantity(dto.getQuantity());
            item.setUnit(dto.getUnit());
            item.setUnitPrice(dto.getUnitPrice());
            return item;
        }).collect(Collectors.toList());

        Order order = Order.builder()
                .recipient(recipient)
                .donor(donor)
                .items(orderItems)
                .totalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : 0.0)
                .status(OrderStatus.PENDING)
                .deliveryAddress(request.getDeliveryAddress())
                .instructions(request.getInstructions())
                .build();
        Order saved = orderRepository.save(order);
        log.info("Order [{}] created by recipient [{}]", saved.getId(), recipientEmail);
        return toResponse(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRecipient(String recipientId) {
        return orderRepository.findByRecipientId(recipientId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByDonor(String donorId) {
        return orderRepository.findByDonorId(donorId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    // ─── Update ───────────────────────────────────────────────────────────

    @Transactional
    public OrderResponse updateOrder(String id, OrderRequest request) {
        Order order = findById(id);
        List<OrderItem> orderItems = request.getItems().stream().map(dto -> {
            FoodItem foodItem = foodItemRepository.findById(dto.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", dto.getFoodItemId()));
            OrderItem item = new OrderItem();
            item.setFoodItem(foodItem);
            item.setFoodItemName(dto.getFoodItemName() != null ? dto.getFoodItemName() : foodItem.getName());
            item.setQuantity(dto.getQuantity());
            item.setUnit(dto.getUnit());
            item.setUnitPrice(dto.getUnitPrice());
            return item;
        }).collect(Collectors.toList());
        order.getItems().clear();
        order.getItems().addAll(orderItems);
        order.setTotalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : 0.0);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setInstructions(request.getInstructions());
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(String id, OrderStatus status, String deliveryPartnerId) {
        Order order = findById(id);
        order.setStatus(status);
        order.setStatusUpdatedAt(Instant.now());
        if (deliveryPartnerId != null && !deliveryPartnerId.isBlank()) {
            DeliveryPartner partner = deliveryPartnerRepository.findById(deliveryPartnerId)
                    .orElseThrow(() -> new ResourceNotFoundException("DeliveryPartner", "id", deliveryPartnerId));
            order.setDeliveryPartner(partner);
        }
        log.info("Order [{}] status updated to [{}]", id, status);
        return toResponse(orderRepository.save(order));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        orderRepository.deleteById(id);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    public OrderResponse toResponse(Order o) {
        List<OrderItemDto> itemDtos = o.getItems().stream().map(item -> OrderItemDto.builder()
                .id(item.getId())
                .foodItemId(item.getFoodItem() != null ? item.getFoodItem().getId() : null)
                .foodItemName(item.getFoodItemName())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .unitPrice(item.getUnitPrice())
                .build()).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .recipientId(o.getRecipient() != null ? o.getRecipient().getId() : null)
                .donorId(o.getDonor() != null ? o.getDonor().getId() : null)
                .deliveryPartnerId(o.getDeliveryPartner() != null ? o.getDeliveryPartner().getId() : null)
                .items(itemDtos)
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .deliveryAddress(o.getDeliveryAddress())
                .instructions(o.getInstructions())
                .statusUpdatedAt(o.getStatusUpdatedAt())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
