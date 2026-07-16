package com.foodbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the FoodBridge Spring Boot application.
 *
 * <p>FoodBridge is a Smart Food Waste Reduction &amp; Donation Platform that connects
 * food donors (restaurants, households, events) with recipients (NGOs, shelters,
 * individuals in need).
 *
 * <p>Bootstrapped with:
 * <ul>
 *   <li>Java 21</li>
 *   <li>Spring Boot 3.3.5</li>
 *   <li>MySQL (Spring Data JPA / Hibernate)</li>
 *   <li>Spring Security 6 with JWT authentication</li>
 * </ul>
 */
@SpringBootApplication
public class FoodBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodBridgeApplication.class, args);
    }
}
