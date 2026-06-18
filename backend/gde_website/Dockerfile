# backend/Dockerfile
# Build the Spring Boot application with Maven using Maven Wrapper
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN chmod +x mvnw && ./mvnw -B clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
