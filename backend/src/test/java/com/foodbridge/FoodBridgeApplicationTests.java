package com.foodbridge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Spring Boot application context load test.
 *
 * <p>Verifies that the full Spring application context starts up
 * without errors. This is the Phase 1 smoke test: if the context
 * fails to load (missing beans, misconfigured properties, etc.),
 * this test catches it immediately.
 *
 * <p>Uses the {@code dev} Spring profile. The MongoDB URI is supplied
 * via the main application.yml; no external relational database is
 * required after the migration to MongoDB Atlas.
 */
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        // Point tests at the same Atlas cluster as dev (override via env if needed)
        "spring.data.mongodb.uri=mongodb+srv://foodbridge:foodbridge2026@cluster0.je6qya4.mongodb.net/foodbridge?retryWrites=true&w=majority&appName=Cluster0"
})
class FoodBridgeApplicationTests {

    /**
     * Verifies that the Spring application context loads successfully.
     * An empty test body is intentional – if the context fails,
     * Spring throws an exception before the test body runs.
     */
    @Test
    void contextLoads() {
        // Context load verification – no assertions needed
    }
}
