package com.foodbridge.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger UI configuration for FoodBridge.
 *
 * <p>Accessible at:
 * <ul>
 *   <li>Swagger UI: {@code http://localhost:8081/api/swagger-ui.html}</li>
 *   <li>Raw JSON spec: {@code http://localhost:8081/api/api-docs}</li>
 * </ul>
 *
 * <p>JWT Bearer security scheme is registered globally. Use the Swagger UI
 * "Authorize" button after obtaining a token from {@code POST /auth/login}.
 * Paste the token as: {@code Bearer <your-token>}
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI foodBridgeOpenApi() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers())
                // Apply JWT auth globally to all operations (override per-endpoint with @SecurityRequirements)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, buildBearerSecurityScheme()));
    }

    private Info buildApiInfo() {
        return new Info()
                .title("FoodBridge API")
                .description("""
                        ## FoodBridge – Smart Food Waste Reduction & Donation Platform
                        
                        A production-ready RESTful API connecting food donors (restaurants, households, events)
                        with recipients (NGOs, shelters, individuals in need).
                        
                        ### Quick Start
                        1. **Register** → `POST /auth/register` (no auth required)
                        2. **Login** → `POST /auth/login` to get your JWT access token
                        3. Click **Authorize** above and enter: `Bearer <your-token>`
                        4. Call any protected endpoint
                        
                        ### Available Modules
                        | Module | Description |
                        |--------|-------------|
                        | 🔐 Authentication | Register, login, refresh token, logout |
                        | 👤 Users | CRUD, role management, profile |
                        | 🍱 Food Items | Listings, status lifecycle |
                        | 🤝 Donations | End-to-end donation flow |
                        | 📦 Orders | Order placement and tracking |
                        | 🏪 Restaurants | Restaurant profile management |
                        | 🚚 Delivery Partners | Availability and assignment |
                        | ⭐ Reviews | Ratings for users and restaurants |
                        | 🔔 Notifications | In-app alerts |
                        | 💳 Payments | Payment initiation and status |
                        
                        ### Authentication
                        All endpoints except `/auth/**` and public GET food/restaurant endpoints require
                        a valid JWT Bearer token. Tokens expire after **24 hours**; use the refresh token
                        endpoint to obtain a new one without re-logging in.
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("FoodBridge Development Team")
                        .email("dev@foodbridge.app")
                        .url("https://github.com/your-username/FoodBridge"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8081/api")
                        .description("Local Development Server (port 8081)"),
                new Server()
                        .url("https://api.foodbridge.app")
                        .description("Production Server (placeholder)")
        );
    }

    private SecurityScheme buildBearerSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name(BEARER_SCHEME_NAME)
                .description("""
                        Enter your JWT access token from `POST /auth/login`.
                        
                        **Format:** `Bearer <token>` (include the "Bearer " prefix)
                        
                        **Example:** `Bearer eyJhbGciOiJIUzI1NiJ9...`
                        
                        Tokens expire after 24 hours. Use `POST /auth/refresh` to renew.
                        """);
    }
}
