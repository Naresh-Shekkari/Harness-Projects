# ==============================================================================
# STAGE 1: Build & Package Java Application using Maven
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build final jar
COPY src ./src
RUN mvn package -DskipTests -B

# ==============================================================================
# STAGE 2: Lightweight Runtime Environment
# ==============================================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for enterprise banking security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy jar from build stage
COPY --from=builder /app/target/auth-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
