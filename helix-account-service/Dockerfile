# File: helix-account-service/Dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the pre-built jar (built by CI/Maven before docker build runs)
COPY target/helix-account-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]