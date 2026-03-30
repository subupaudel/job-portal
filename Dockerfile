FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy only required files first (faster + stable)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# Download dependencies first (important)
RUN ./gradlew dependencies

# Now copy full project
COPY . .

# Build project
RUN ./gradlew build -x test

# Run app
CMD ["sh", "-c", "java -jar build/libs/*.jar"]
