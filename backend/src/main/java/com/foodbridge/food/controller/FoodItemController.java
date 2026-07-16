package com.foodbridge.food.controller;

import com.foodbridge.common.response.ApiResponse;
import com.foodbridge.food.dto.FoodItemRequest;
import com.foodbridge.food.dto.FoodItemResponse;
import com.foodbridge.food.model.FoodStatus;
import com.foodbridge.food.service.FoodItemService;
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
 * REST controller for food item management.
 * Base path: /v1/food-items
 */
@RestController
@RequestMapping("/v1/food-items")
@RequiredArgsConstructor
@Tag(name = "Food Items", description = "Manage food item listings for donation")
public class FoodItemController {

    private final FoodItemService foodItemService;

    @GetMapping
    @Operation(summary = "Get all food items (public)")
    public ResponseEntity<ApiResponse<List<FoodItemResponse>>> getAllFoodItems() {
        return ResponseEntity.ok(ApiResponse.success("Food items retrieved.", foodItemService.getAllFoodItems()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get food item by ID (public)")
    public ResponseEntity<ApiResponse<FoodItemResponse>> getFoodItemById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Food item retrieved.", foodItemService.getFoodItemById(id)));
    }

    @GetMapping("/donor/{donorId}")
    @Operation(summary = "Get food items by donor ID")
    public ResponseEntity<ApiResponse<List<FoodItemResponse>>> getByDonor(@PathVariable String donorId) {
        return ResponseEntity.ok(ApiResponse.success("Food items retrieved.", foodItemService.getFoodItemsByDonor(donorId)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get food items by status")
    public ResponseEntity<ApiResponse<List<FoodItemResponse>>> getByStatus(@PathVariable FoodStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Food items retrieved.", foodItemService.getFoodItemsByStatus(status)));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get food items by category")
    public ResponseEntity<ApiResponse<List<FoodItemResponse>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success("Food items retrieved.", foodItemService.getFoodItemsByCategory(category)));
    }

    @PostMapping
    @Operation(summary = "Create a new food item listing")
    public ResponseEntity<ApiResponse<FoodItemResponse>> createFoodItem(
            @Valid @RequestBody FoodItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // donorId resolved from authenticated user's email via service layer if needed;
        // here we pass the email which can be resolved in service
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Food item created.", foodItemService.createFoodItem(request, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a food item")
    public ResponseEntity<ApiResponse<FoodItemResponse>> updateFoodItem(
            @PathVariable String id,
            @Valid @RequestBody FoodItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Food item updated.", foodItemService.updateFoodItem(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update food item status")
    public ResponseEntity<ApiResponse<FoodItemResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam FoodStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated.", foodItemService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a food item")
    public ResponseEntity<ApiResponse<Void>> deleteFoodItem(@PathVariable String id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.ok(ApiResponse.success("Food item deleted."));
    }
}
