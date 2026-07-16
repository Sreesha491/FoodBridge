package com.foodbridge.restaurant.repository;

import com.foodbridge.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Restaurant} entities.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    /** All restaurants owned by a specific user. */
    List<Restaurant> findByOwnerId(String ownerId);

    /** All active restaurants. */
    List<Restaurant> findByActiveTrue();

    /** Restaurants in a specific city. */
    List<Restaurant> findByCity(String city);

    /** Check if a restaurant with the given name and city exists. */
    boolean existsByNameAndCity(String name, String city);
}
