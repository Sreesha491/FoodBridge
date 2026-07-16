package com.foodbridge.food.service;

import com.foodbridge.common.exception.BadRequestException;
import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.food.dto.FoodItemRequest;
import com.foodbridge.food.dto.FoodItemResponse;
import com.foodbridge.food.model.FoodItem;
import com.foodbridge.food.model.FoodStatus;
import com.foodbridge.food.repository.FoodItemRepository;
import com.foodbridge.restaurant.model.Restaurant;
import com.foodbridge.restaurant.repository.RestaurantRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer for food item management.
 *
 * <p>Food items represent surplus food available for donation. They can be
 * created by donors (individuals, households) or restaurants.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    // ─── Create ───────────────────────────────────────────────────────────

    /**
     * Creates a food item listing. The donor is resolved from the authenticated
     * user's email address (Spring Security principal).
     *
     * @param request    food item details
     * @param donorEmail authenticated user's email from Spring Security context
     * @return created food item DTO
     */
    @Transactional
    public FoodItemResponse createFoodItem(FoodItemRequest request, String donorEmail) {
        User donor = userRepository.findByEmail(donorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", donorEmail));

        Restaurant restaurant = null;
        if (request.getRestaurantId() != null && !request.getRestaurantId().isBlank()) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));
        }

        FoodItem foodItem = FoodItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .expiryDate(request.getExpiryDate())
                .donor(donor)
                .restaurant(restaurant)
                .status(FoodStatus.AVAILABLE)
                .imageUrl(request.getImageUrl())
                .pickupAddress(request.getPickupAddress())
                .specialInstructions(request.getSpecialInstructions())
                .build();

        FoodItem saved = foodItemRepository.save(foodItem);
        log.info("Food item [{}] created by donor [{}]", saved.getId(), donorEmail);
        return toResponse(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FoodItemResponse> getAllFoodItems() {
        return foodItemRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FoodItemResponse getFoodItemById(String id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<FoodItemResponse> getFoodItemsByDonor(String donorId) {
        return foodItemRepository.findByDonorId(donorId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FoodItemResponse> getFoodItemsByStatus(FoodStatus status) {
        return foodItemRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category).stream().map(this::toResponse).toList();
    }

    // ─── Update ───────────────────────────────────────────────────────────

    @Transactional
    public FoodItemResponse updateFoodItem(String id, FoodItemRequest request) {
        FoodItem item = findById(id);

        Restaurant restaurant = null;
        if (request.getRestaurantId() != null && !request.getRestaurantId().isBlank()) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));
        }

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        item.setExpiryDate(request.getExpiryDate());
        item.setRestaurant(restaurant);
        item.setImageUrl(request.getImageUrl());
        item.setPickupAddress(request.getPickupAddress());
        item.setSpecialInstructions(request.getSpecialInstructions());

        log.debug("Food item [{}] updated", id);
        return toResponse(foodItemRepository.save(item));
    }

    @Transactional
    public FoodItemResponse updateStatus(String id, FoodStatus status) {
        FoodItem item = findById(id);
        item.setStatus(status);
        log.debug("Food item [{}] status updated to [{}]", id, status);
        return toResponse(foodItemRepository.save(item));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteFoodItem(String id) {
        if (!foodItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("FoodItem", "id", id);
        }
        foodItemRepository.deleteById(id);
        log.info("Food item [{}] deleted", id);
    }

    // ─── Mapping ──────────────────────────────────────────────────────────

    private FoodItem findById(String id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", id));
    }

    public FoodItemResponse toResponse(FoodItem f) {
        return FoodItemResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .category(f.getCategory())
                .quantity(f.getQuantity())
                .unit(f.getUnit())
                .expiryDate(f.getExpiryDate())
                .donorId(f.getDonor() != null ? f.getDonor().getId() : null)
                .restaurantId(f.getRestaurant() != null ? f.getRestaurant().getId() : null)
                .status(f.getStatus())
                .imageUrl(f.getImageUrl())
                .pickupAddress(f.getPickupAddress())
                .specialInstructions(f.getSpecialInstructions())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }
}
