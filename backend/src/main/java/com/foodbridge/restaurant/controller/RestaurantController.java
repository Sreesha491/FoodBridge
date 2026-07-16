package com.foodbridge.restaurant.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.restaurant.dto.RestaurantRequest;
import com.foodbridge.restaurant.dto.RestaurantResponse;
import com.foodbridge.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for restaurant management.
 * Base path: /v1/restaurants
 */
@RestController
@RequestMapping("/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Manage restaurant profiles on the platform")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "Get all restaurants (public)")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAllRestaurants() {
        return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved.", restaurantService.getAllRestaurants()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active restaurants (public)")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getActiveRestaurants() {
        return ResponseEntity.ok(ApiResponse.success("Active restaurants retrieved.", restaurantService.getActiveRestaurants()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID (public)")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getRestaurantById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Restaurant retrieved.", restaurantService.getRestaurantById(id)));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get restaurants by owner ID")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved.", restaurantService.getRestaurantsByOwner(ownerId)));
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get restaurants by city (public)")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved.", restaurantService.getRestaurantsByCity(city)));
    }

    @PostMapping
    @Operation(summary = "Register a new restaurant")
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
            @Valid @RequestBody RestaurantRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant registered.", restaurantService.createRestaurant(request, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant details")
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated.", restaurantService.updateRestaurant(id, request)));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Toggle restaurant active status")
    public ResponseEntity<ApiResponse<RestaurantResponse>> toggleActive(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Restaurant status toggled.", restaurantService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a restaurant")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(@PathVariable String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant deleted."));
    }
}
