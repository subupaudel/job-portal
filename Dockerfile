# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .

# Build executable Spring Boot jar
RUN gradle clean bootJar -x test

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy only the executable jar
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
