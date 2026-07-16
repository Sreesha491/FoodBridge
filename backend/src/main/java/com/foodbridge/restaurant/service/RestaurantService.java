package com.foodbridge.restaurant.service;

import com.foodbridge.common.exception.BadRequestException;
import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.restaurant.dto.RestaurantRequest;
import com.foodbridge.restaurant.dto.RestaurantResponse;
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
 * Business logic layer for restaurant management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // ─── Create ───────────────────────────────────────────────────────────

    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request, String ownerEmail) {
        if (restaurantRepository.existsByNameAndCity(request.getName(), request.getCity())) {
            throw new BadRequestException("A restaurant with this name already exists in " + request.getCity());
        }
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", ownerEmail));
        Restaurant restaurant = Restaurant.builder()
                .owner(owner)
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .cuisineTypes(request.getCuisineTypes())
                .phone(request.getPhone())
                .email(request.getEmail())
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .active(true)
                .build();
        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurant [{}] created by owner [{}]", saved.getId(), ownerEmail);
        return toResponse(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(String id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getActiveRestaurants() {
        return restaurantRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByOwner(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCity(city).stream().map(this::toResponse).toList();
    }

    // ─── Update ───────────────────────────────────────────────────────────

    @Transactional
    public RestaurantResponse updateRestaurant(String id, RestaurantRequest request) {
        Restaurant restaurant = findById(id);
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setCuisineTypes(request.getCuisineTypes());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setLogoUrl(request.getLogoUrl());
        restaurant.setDescription(request.getDescription());
        return toResponse(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantResponse toggleActive(String id) {
        Restaurant restaurant = findById(id);
        restaurant.setActive(!restaurant.isActive());
        return toResponse(restaurantRepository.save(restaurant));
    }

    // ─── Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteRestaurant(String id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant", "id", id);
        }
        restaurantRepository.deleteById(id);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private Restaurant findById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
    }

    public RestaurantResponse toResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .ownerId(r.getOwner() != null ? r.getOwner().getId() : null)
                .name(r.getName())
                .address(r.getAddress())
                .city(r.getCity())
                .cuisineTypes(r.getCuisineTypes())
                .phone(r.getPhone())
                .email(r.getEmail())
                .rating(r.getRating())
                .ratingCount(r.getRatingCount())
                .logoUrl(r.getLogoUrl())
                .description(r.getDescription())
                .active(r.isActive())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
