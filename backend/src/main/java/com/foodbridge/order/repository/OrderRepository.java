package com.foodbridge.order.repository;

import com.foodbridge.order.model.Order;
import com.foodbridge.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Order} entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /** All orders placed by a recipient. */
    List<Order> findByRecipientId(String recipientId);

    /** All orders assigned to a donor/restaurant. */
    List<Order> findByDonorId(String donorId);

    /** All orders in a given status. */
    List<Order> findByStatus(OrderStatus status);

    /** Orders assigned to a delivery partner. */
    List<Order> findByDeliveryPartnerId(String deliveryPartnerId);

    /** Recipient's orders in a specific status. */
    List<Order> findByRecipientIdAndStatus(String recipientId, OrderStatus status);
}
