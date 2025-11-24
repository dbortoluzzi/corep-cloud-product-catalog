# Docker Compose Hands-on Exercise: Spring Boot + PostgreSQL

> **🎓 This is a hands-on exercise for students**
> 
> **Purpose**: Learn how to create Docker and Docker Compose configuration files from scratch.
> 
> **If you just want to use the existing setup**, see [`README.md`](README.md) instead.

**Objective**: Create a complete Docker Compose setup to containerize the Spring Boot microservice and orchestrate it with PostgreSQL.

**What you'll create**:
- `Dockerfile` for the Spring Boot application
- `docker-compose.yml` for orchestrating app and database services

## Prerequisites
- Docker and Docker Compose v2 installed (Docker Compose is included in Docker Desktop or can be installed as a plugin)
- Maven installed (to compile the project)
- Git installed (to clone the repository)

**Note**: This exercise uses Docker Compose v2 (`docker compose` command). If you have the older v1 (`docker-compose`), you can still use it, but v2 is recommended.

## Project Setup

### Step 1: Clone, Navigate, and Compile the Project

```bash
# Clone the project repository
git clone https://github.com/dbortoluzzi/corep-cloud-product-catalog.git
cd corep-cloud-product-catalog

# Navigate to the monolith folder (source code)
cd 01-monolith

# Compile the project (generates JAR in target/)
mvn clean install

# Verify the JAR was created
ls -la target/product-catalog-service-1.0.0-SNAPSHOT.jar
```

### Step 2: Navigate to the Docker Folder

```bash
# Go back to root and navigate to the docker folder (where you'll work)
cd ../02-docker

# You should find the folder mostly empty or with minimal files
# This is where YOU will create the Docker configuration files
```

## Exercise Tasks

### Step 3: Discover Configuration Requirements

Before creating the Docker files, you need to understand what configuration your application needs. Here's how to discover it:

#### A. Find Spring Boot Profile and Environment Variables

1. **Open** `../01-monolith/src/main/resources/application.yml`
2. **Look for profile sections** (marked with `---`)
3. **Find the `prod` profile** (around line 87) - this is configured for PostgreSQL
4. **Check for environment variable syntax**: `${ENV_VAR:default_value}`
   - Example: `${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/productdb}`
   - This means: use `SPRING_DATASOURCE_URL` env var, or the default if not set
5. **List all environment variables** you need to override:
   - `SPRING_PROFILES_ACTIVE` (to activate `prod` profile)
   - `SPRING_DATASOURCE_URL` (to point to Docker service `db`)
   - `SPRING_DATASOURCE_USERNAME` (to match PostgreSQL user)
   - `SPRING_DATASOURCE_PASSWORD` (to match PostgreSQL password)

#### B. Find Health Check Endpoint

1. **In the same `application.yml`**, look for `management` section (around line 64)
2. **Check** `management.endpoints.web.exposure.include` - this lists available endpoints
3. **Verify** that `health` is included
4. **Test manually** (optional):
   ```bash
   cd ../01-monolith
   ./mvnw spring-boot:run
   # In another terminal:
   curl http://localhost:8080/actuator/health
   ```
5. **Health check URL**: `/actuator/health`

#### C. Find PostgreSQL Docker Image Environment Variables

**Important**: These are NOT from your application - they come from the Docker image!

1. **Check Docker Hub documentation**:
   - Go to https://hub.docker.com/_/postgres
   - Look for "Environment Variables" section
   - Standard variables:
     - `POSTGRES_DB`: Name of default database to create
     - `POSTGRES_USER`: Database username (default: `postgres`)
     - `POSTGRES_PASSWORD`: Password for user (REQUIRED)
     - `PGDATA`: Custom data directory (optional)

2. **Match with Spring Boot**: The values you set here must match what you configure in Spring Boot datasource:
   - `POSTGRES_DB` → database name in JDBC URL
   - `POSTGRES_USER` → `SPRING_DATASOURCE_USERNAME`
   - `POSTGRES_PASSWORD` → `SPRING_DATASOURCE_PASSWORD`

#### D. Find PostgreSQL Health Check Command

- PostgreSQL Docker images include `pg_isready` command
- Check format: `pg_isready -U <username> -d <database>`
- You'll use the same username and database from your `db` service configuration

### Step 4: Prepare the JAR for Docker Build

Before creating the Dockerfile, copy the compiled JAR to the `02-docker` directory:

```bash
# Copy the JAR file to current directory
cp ../01-monolith/target/product-catalog-service-1.0.0-SNAPSHOT.jar app.jar
```

**Note**: The Docker build context is the `02-docker` directory, so the JAR must be in this directory.

### Step 5: Create the Dockerfile

In the `02-docker` directory, create a file named `Dockerfile`:

**Requirements:**
- Use `eclipse-temurin:17-jre-alpine` as base image (lightweight Java runtime)
- Install `wget` for HEALTHCHECK (use `RUN apk add --no-cache wget`)
- Set WORKDIR to `/app`
- Copy `app.jar` from current directory to `/app/app.jar` in the container
- EXPOSE port 8080
- Add a HEALTHCHECK using wget to verify the `/actuator/health` endpoint
  - interval: 30s
  - timeout: 3s
  - retries: 3
- Set CMD to run: `["java", "-jar", "app.jar"]`

**Hints:**
- The JAR file (`app.jar`) should be in the same directory as the Dockerfile
- Use Alpine base image for minimal size
- HEALTHCHECK is critical for Docker Compose dependency management

### Step 6: Create the docker-compose.yml

In the `02-docker` directory, create a file named `docker-compose.yml`:

**Requirements for `app` service:**
- Build from the Dockerfile in current directory (context: `.`, dockerfile: `Dockerfile`)
- Container name: `product-catalog-app`
- Expose port 8080 on host (map to 8080 in container)
- Environment variables (activate `prod` profile and override datasource settings):
  - `SPRING_PROFILES_ACTIVE`: prod (uses PostgreSQL configuration from application.yml)
  - `SPRING_DATASOURCE_URL`: jdbc:postgresql://db:5432/catalog_db
  - `SPRING_DATASOURCE_USERNAME`: catalog_user
  - `SPRING_DATASOURCE_PASSWORD`: catalog_password
- Depend on `db` service with `condition: service_healthy`
- Connect to custom network named `appnet`
- Include healthcheck configuration

**Hints:**
- Use the environment variables you discovered in Step 3
- The `prod` profile is already configured in `application.yml` with PostgreSQL settings
- Only need to override datasource connection details for Docker Compose
- Service name `db` is automatically resolved via Docker DNS (use service name as hostname)
- For healthcheck: use the endpoint you discovered in Step 3 (e.g., `/actuator/health`)

**Requirements for `db` service:**
- Image: `postgres:15-alpine`
- Container name: `product-catalog-postgres`
- Environment variables (from PostgreSQL Docker image documentation):
  - `POSTGRES_DB`: catalog_db (name of database to create)
  - `POSTGRES_USER`: catalog_user (database username)
  - `POSTGRES_PASSWORD`: catalog_password (REQUIRED - container won't start without it)
  - `PGDATA`: /var/lib/postgresql/data/pgdata (optional, custom data directory)
- Expose port 5432 on host (for debugging)
- Mount named volume `db_data` to `/var/lib/postgresql/data` (for persistence)
- Connect to custom network `appnet`
- Include healthcheck using `pg_isready` command

**Top-level requirements:**
- Create named volume `db_data` with local driver
- Create custom bridge network `appnet`

**Note**: Docker Compose v2 doesn't require a `version` field. If you're using v1, you can add `version: '3.8'` at the top, but it's optional in v2.

**Hints:**
- Service names become hostnames (e.g., service `db` is reachable as `db:5432`)
- Healthchecks ensure services start in correct order
- Named volumes persist data across container restarts
- Custom networks provide automatic DNS resolution
- For PostgreSQL healthcheck: use `pg_isready` command you discovered in Step 3

### Step 7: Build and Start the Environment

```bash
# Build images and start services in background
docker compose up -d --build

# Verify both services are running
docker compose ps

# Check for "healthy" status on both services
```

### Step 8: Test the Application

```bash
# Test basic connectivity
curl http://localhost:8080/

# Test Spring Boot Actuator
curl http://localhost:8080/actuator/health

# Test the API (check your app's actual endpoints)
curl http://localhost:8080/api/v1/products

# Check app logs
docker compose logs -f app
```

### Step 9: Clean Up

```bash
# Stop and remove all services (keep volumes for data persistence)
docker compose down

# Stop and remove services + delete volumes (careful! data loss)
docker compose down -v
```

## Expected Outcomes

- Both `app` and `db` containers running successfully  
- Spring Boot app accessible on http://localhost:8080  
- Application successfully connects to PostgreSQL via hostname `db`  
- Database data persists data
- Health checks pass for both services  
- Logs show successful initialization of both services  
- Service-to-service communication works via DNS resolution  


## Troubleshooting & Debugging

### Common Issues

**Container won't start**:
- Check logs: `docker compose logs app` or `docker compose logs db`
- Verify JAR file exists: `ls -la app.jar`
- Check if ports are already in use: `netstat -tuln | grep 8080` or `netstat -tuln | grep 5432`

**Database connection errors**:
- Verify `db` service is healthy: `docker compose ps`
- Check network connectivity: `docker compose exec app ping db`
- Verify environment variables match between `app` and `db` services

**Health check failures**:
- Test endpoint manually: `curl http://localhost:8080/actuator/health`
- Check if Actuator is enabled in `application.yml`

### Debugging Commands

```bash
# View all container details
docker compose ps -a

# Stream logs from specific service
docker compose logs -f app
docker compose logs -f db

# View last N lines of logs
docker compose logs --tail=50 app

# Execute command in running container
docker compose exec app sh
docker compose exec db psql -U catalog_user -d catalog_db

# Inspect network
docker network inspect 02-docker_appnet

# View volume details
docker volume inspect 02-docker_db_data

# Restart a specific service
docker compose restart app

# Rebuild and restart
docker compose up -d --build
```

