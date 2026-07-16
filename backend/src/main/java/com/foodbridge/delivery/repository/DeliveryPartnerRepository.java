package com.foodbridge.delivery.repository;

import com.foodbridge.delivery.model.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link DeliveryPartner} entities.
 */
@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, String> {

    /** Find by linked user account ID. */
    Optional<DeliveryPartner> findByUserId(String userId);

    /** All delivery partners currently available for assignment. */
    List<DeliveryPartner> findByAvailableTrue();

    /** Available partners in a specific operating area. */
    List<DeliveryPartner> findByAvailableTrueAndOperatingArea(String operatingArea);

    /** Check if a profile already exists for this user. */
    boolean existsByUserId(String userId);
}
