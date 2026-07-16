package com.foodbridge.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.foodbridge.food.model.FoodItem;

/**
 * JPA entity representing a single line item within an {@link Order}.
 * Stored in the order_items table, linked to orders via order_id.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Food item reference. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    /** Name snapshot at time of order (denormalised). */
    private String foodItemName;

    /** Quantity ordered. */
    private Double quantity;

    /** Unit (kg, pieces, etc.). */
    private String unit;

    /** Unit price at time of order (0 for free donations). */
    private Double unitPrice;
}
