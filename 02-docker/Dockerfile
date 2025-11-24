# Dockerfile for Product Catalog Service
# Base image: Eclipse Temurin 17 JRE (Alpine for minimal size)
FROM eclipse-temurin:17-jre-alpine

# Install wget for HEALTHCHECK
RUN apk add --no-cache wget

# Set working directory
WORKDIR /app

# Copy the JAR file (must be copied to 02-docker before build)
# Students should copy: cp ../01-monolith/target/product-catalog-service-1.0.0-SNAPSHOT.jar app.jar
COPY app.jar app.jar

# Expose port 8080
EXPOSE 8080

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["java", "-jar", "app.jar"]
