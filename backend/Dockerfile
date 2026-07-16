# ──────────────────────────────────────────────────────────────
#  FoodBridge Backend – Dockerfile
#  Multi-stage build: builder (Maven) → runtime (JRE 21 slim)
# ──────────────────────────────────────────────────────────────

# ── Stage 1: Build ────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Cache Maven dependencies before copying source
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B 2>/dev/null || true

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B --no-transfer-progress

# ── Stage 2: Runtime ──────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Non-root user for security
RUN addgroup -S foodbridge && adduser -S foodbridge -G foodbridge

WORKDIR /app

# Copy the fat JAR from builder
COPY --from=builder /build/target/*.jar app.jar

# Change ownership
RUN chown -R foodbridge:foodbridge /app
USER foodbridge

EXPOSE 8081

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}", \
  "-jar", "app.jar"]
