# ---------- Build Stage ----------
FROM gradle:8.14-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build Spring Boot jar (skip tests to speed up)
RUN ./gradlew clean bootJar -x test

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre AS run

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port (same as Spring Boot server.port)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]