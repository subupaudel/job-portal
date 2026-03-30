# Build stage
FROM gradle:8.14-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
