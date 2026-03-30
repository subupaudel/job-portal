# Use Java 17 (recommended for Spring Boot)
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Give permission to gradlew
RUN chmod +x ./gradlew

# Build the project (skip tests for faster deploy)
RUN ./gradlew build -x test

# Expose port (Render uses dynamic PORT)
EXPOSE 8080

# Run the jar file
CMD ["sh", "-c", "java -jar build/libs/*.jar"]
