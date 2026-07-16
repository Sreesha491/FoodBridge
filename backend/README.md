# FoodBridge Backend

A robust, production-ready Spring Boot backend for the FoodBridge platform. It securely connects donors, NGOs, and delivery partners to minimize food waste.

## 🚀 Features
- **Secure Authentication:** JWT-based stateless authentication with Refresh Tokens and BCrypt password hashing.
- **Role-Based Access Control:** Distinct roles for `ADMIN`, `DONOR`, `NGO`, `RESTAURANT`, and `DELIVERY_PARTNER`.
- **Relational Data Integrity:** Well-normalized MySQL schema with proper JPA associations (`@ManyToOne`, `@OneToMany`) and optimized indexes.
- **Consistent API Standards:** Every endpoint guarantees a predictable `ApiResponse<T>` envelope for seamless frontend consumption.
- **Global Error Handling:** Graceful, unhandled exception interception returning meaningful HTTP codes via `@ControllerAdvice`.
- **Interactive Documentation:** Out-of-the-box Swagger UI via OpenAPI 3.

## 🛠 Tech Stack
- **Java 21**
- **Spring Boot 3.3.x** (Web, Data JPA, Security, Validation)
- **MySQL 8**
- **JJWT** (JSON Web Tokens)
- **Maven**

## 📦 Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd FoodBridge/backend
   ```

2. **Configure Database Settings:**
   Ensure MySQL is running on port `3306`. Update your `src/main/resources/application.yml` if your credentials differ:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/foodbridge?createDatabaseIfNotExist=true
       username: root
       password: rootpassword
   ```

3. **Run the Application:**
   ```bash
   ./mvnw clean spring-boot:run
   ```
   *Note: The server is configured to run on port `8081`.*

## 📖 API Documentation
Once the server is running, the interactive Swagger documentation will be available at:
- **Swagger UI:** `http://localhost:8081/swagger-ui.html`

> **Note:** Use the `/api/v1/auth/login` endpoint to obtain a JWT. Click the "Authorize" button in Swagger UI to attach your Bearer token and test protected endpoints!

## 🧪 Testing
Run the automated test suite using Maven:
```bash
./mvnw clean test
```

---
*Built as a professional, production-ready system suitable for final year projects and internships.*
