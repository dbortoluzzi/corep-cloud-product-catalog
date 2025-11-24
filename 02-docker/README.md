# Phase 2: Docker

> **Status**: ✅ Ready

This phase demonstrates containerization of the monolith application using Docker and Docker Compose.

## Overview

This directory contains Docker configuration files to containerize the Spring Boot application and orchestrate it with PostgreSQL using Docker Compose.

> **📚 Documentation vs Exercise**: 
> - **This README.md**: Reference documentation for using the existing Docker setup
> - **EXERCISE.md**: Step-by-step hands-on exercise for students to create the Docker files from scratch

## Files

- **`Dockerfile`**: Multi-stage Docker image for the Spring Boot application
- **`docker-compose.yml`**: Orchestration file defining app and database services

## Prerequisites

- Docker and Docker Compose v2 installed (Docker Compose is included in Docker Desktop or can be installed as a plugin)
- Maven installed (to compile the project)
- The JAR file must be built in `../01-monolith/target/`

**Note**: This project uses Docker Compose v2 (`docker compose` command). If you have the older v1 (`docker-compose`), you can still use it, but v2 is recommended.

## Quick Start

### 1. Build the JAR (if not already built)

```bash
cd ../01-monolith
./mvnw clean package -DskipTests
cd ../02-docker
```

### 2. Prepare the JAR for Docker Build

```bash
# Copy the JAR to current directory (required for Docker build context)
cp ../01-monolith/target/product-catalog-service-1.0.0-SNAPSHOT.jar app.jar
```

**Note**: The Docker build context is the `02-docker` directory, so the JAR must be copied here before building.

### 3. Start the Environment

```bash
# Build images and start services in background
docker compose up -d --build

# View logs
docker compose logs -f app
```

### 4. Verify Services

```bash
# Check service status
docker compose ps

# Test application health
curl http://localhost:8080/actuator/health

# Test API endpoint
curl http://localhost:8080/api/v1/products
```

### 5. Stop Services

```bash
# Stop services (keeps volumes)
docker compose down

# Stop services and remove volumes (data loss)
docker compose down -v
```

## Architecture

### Services

1. **`app`** (product-catalog-app)
   - Spring Boot application
   - Port: 8080
   - Depends on `db` service
   - Health check: `/actuator/health`

2. **`db`** (product-catalog-postgres)
   - PostgreSQL 15 (Alpine)
   - Port: 5432
   - Database: `catalog_db`
   - Persistent volume: `db_data`

### Network

- Custom bridge network: `appnet`
- Automatic DNS resolution (service names as hostnames)

### Volumes

- `db_data`: Persistent storage for PostgreSQL data

## Configuration

The application uses the `prod` Spring Boot profile configured in `application.yml`. The Docker Compose setup overrides datasource connection details to connect to the PostgreSQL container.

**Key configuration values**:
- Spring Boot profile: `prod`
- Database host: `db` (Docker service name, resolved via DNS)
- Database: `catalog_db`
- User: `catalog_user`
- Password: `catalog_password`

**For detailed explanation of how to discover these values**, see [`EXERCISE.md`](EXERCISE.md) Step 3.

## Debugging

For debugging commands and troubleshooting, see the "Debugging Commands" section in [`EXERCISE.md`](EXERCISE.md).

**Quick commands**:
```bash
# View logs
docker compose logs -f app

# Check service status
docker compose ps
```

## Exercise

**For students**: If you want to learn how to create these Docker files from scratch, follow the step-by-step guide in [`EXERCISE.md`](EXERCISE.md).

**For quick usage**: Use the Quick Start section above to run the existing Docker setup.

---

**See**: [`../01-monolith/README.md`](../01-monolith/README.md) for Phase 1 documentation.
