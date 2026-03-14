# -------------------------------
# Stage 1: Build the application
# -------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn -B dependency:go-offline

# Copy source code
COPY src ./src

# Build the Spring Boot jar
RUN mvn -B clean package -DskipTests


# --------------------------------
# Stage 2: Runtime (Distroless)
# --------------------------------
FROM gcr.io/distroless/java21-debian12

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run application
ENTRYPOINT ["java","-jar","/app/app.jar"]
