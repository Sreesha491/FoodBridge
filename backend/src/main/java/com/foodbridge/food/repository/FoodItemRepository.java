package com.foodbridge.food.repository;

import com.foodbridge.food.model.FoodItem;
import com.foodbridge.food.model.FoodStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link FoodItem} entities.
 */
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, String> {

    /** All food items listed by a specific donor. */
    List<FoodItem> findByDonorId(String donorId);

    /** All food items with a given status. */
    List<FoodItem> findByStatus(FoodStatus status);

    /** All food items by donor with a given status. */
    List<FoodItem> findByDonorIdAndStatus(String donorId, FoodStatus status);

    /** All food items by restaurant. */
    List<FoodItem> findByRestaurantId(String restaurantId);

    /** All food items in a given category. */
    List<FoodItem> findByCategory(String category);
}
