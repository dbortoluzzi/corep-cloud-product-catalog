# Microservices Decomposition Exercise

> **🎓 Hands-on exercise for students**
> 
> **Objective**: Decompose the monolith into microservices with service discovery and API Gateway.

## Prerequisites

- Docker and Docker Compose v2
- Maven 3.6+
- Understanding of Spring Boot basics
- Knowledge from Phase 1 (Monolith) and Phase 2 (Docker)

## Exercise Overview

You will complete 6 tasks:
1. **Extract Catalog Service** - Create product management microservice
2. **Extract Inventory Service** - Create stock management microservice  
3. **Design Inter-Service Communication** - Analyze relationships and communication patterns
4. **Create API Gateway** - Single entry point for all services
5. **Docker Multi-Stage Builds** - Efficient container images
6. **Docker Compose Orchestration** - Orchestrate all services with Consul discovery

**Important**: You will learn to analyze service relationships and choose appropriate communication patterns. Research ACID vs BASE models to understand consistency trade-offs.

## Tasks

### Task 1: Extract Catalog Service

**Goal**: Create a standalone microservice for product catalog.

**Steps**:
1. Create `catalog-service/` directory structure
2. Copy catalog domain code from `../01-monolith/src/main/java/com/corep/productcatalog/catalog/`
3. Create new Spring Boot application class
4. Configure `application.yml` with:
   - Service name: `catalog-service`
   - Port: `8081`
   - Database: PostgreSQL (separate DB)
   - Consul discovery enabled
5. Create `pom.xml` with dependencies:
   - Spring Boot Web, Data JPA
   - PostgreSQL driver
   - Consul Discovery
   - OpenFeign (for future inter-service calls)

**Hint**: Check `../01-monolith/pom.xml` for dependency versions.

### Task 2: Extract Inventory Service

**Goal**: Create a standalone microservice for inventory management.

**Steps**:
1. Create `inventory-service/` directory structure
2. Copy inventory domain code from `../01-monolith/src/main/java/com/corep/productcatalog/inventory/`
3. Create new Spring Boot application class
4. Configure `application.yml` with:
   - Service name: `inventory-service`
   - Port: `8082`
   - Database: PostgreSQL (separate DB)
   - Consul discovery enabled
5. Create `pom.xml` with dependencies:
   - Spring Boot Web, Data JPA
   - PostgreSQL driver
   - Consul Discovery
   - OpenFeign (for inter-service communication)

**Challenge**: Inventory Service needs to validate products exist. How will it communicate with Catalog Service? Think about this before proceeding.

### Task 3: Design Inter-Service Communication

**Goal**: Analyze relationships between services and decide communication patterns.

**Critical Thinking Questions**:

1. **Service Relationships Analysis**:
   - What is the relationship between Catalog and Inventory services?
   - When a product is created, what should happen to inventory?
   - When inventory operations occur, do we need to validate the product exists?

2. **Communication Direction**:
   - Who should call whom?
   - **Option A**: Catalog Service calls Inventory Service (when product is created)
   - **Option B**: Inventory Service calls Catalog Service (when inventory is used)
   - **Option C**: Both directions (bidirectional communication)
   - **Decision**: Which makes more sense? Why?

3. **Consistency Pattern**:
   - In the monolith, product and inventory were in the same database (ACID transactions)
   - In microservices, they are in separate databases
   - **Question**: Can we use ACID transactions across services? Why not?
   - **Alternative**: What pattern should we use? Research ACID vs BASE models.

4. **Where to Place Feign Clients**:
   - Remember: Feign clients are placed in the service that **makes the call**
   - If Service A needs to call Service B → Feign client goes in Service A
   - **Decision**: Based on your analysis in questions 1-3, where should the Feign clients be placed?

**Note**: After answering these questions, you'll implement the Feign clients. For now, focus on understanding the relationships and patterns. Document your decisions.

### Task 4: Create API Gateway

**Goal**: Create a Spring Cloud Gateway as single entry point.

**Steps**:
1. Create `api-gateway/` directory structure
2. Create Spring Boot application with Gateway dependencies
3. Configure routes to forward requests to appropriate services
   - Think about: Which paths should go to which service?
   - Use `lb://` prefix for load balancing with service discovery
4. Configure Consul discovery for dynamic routing
5. Set port: `8080`

**Research**: Look up Spring Cloud Gateway routing configuration. How do you route based on path patterns?

### Task 5: Docker Multi-Stage Builds

**Goal**: Create efficient Dockerfiles with multi-stage builds.

**Requirements**:
- **Build stage**: Use `maven:3.9-eclipse-temurin-17` to compile
- **Runtime stage**: Use `eclipse-temurin:17-jre-alpine` for final image
- Copy only the JAR file between stages
- No manual JAR copying needed (build happens in Docker)

**Example Structure**:
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

**Apply to**: `catalog-service/Dockerfile`, `inventory-service/Dockerfile`, `api-gateway/Dockerfile`

### Task 6: Docker Compose Orchestration

**Goal**: Orchestrate all services with Consul discovery.

**Services to create**:
1. **consul** - Service discovery (port 8500)
2. **catalog-db** - PostgreSQL for catalog service
3. **inventory-db** - PostgreSQL for inventory service
4. **catalog-service** - Catalog microservice
5. **inventory-service** - Inventory microservice
6. **api-gateway** - API Gateway

**Requirements**:
- All services on same network
- Health checks for all services
- Proper `depends_on` with health conditions
- Environment variables for database connections
- Services must register with Consul

**Discovery**: Check `../02-docker/docker-compose.yml` for patterns, but adapt for multiple services.

## Expected Outcome

After completing all tasks:

```bash
# Start everything
docker compose up -d --build

# Test API Gateway
curl http://localhost:8080/api/v1/products

# Check Consul UI
open http://localhost:8500

# Verify services are registered
curl http://localhost:8500/v1/agent/services
```

## Key Learning Points

- **Service Decomposition**: How to split a monolith
- **Service Relationships**: Analyzing dependencies and communication patterns
- **Inter-Service Communication**: Where to place Feign clients and why
- **Consistency Patterns**: ACID vs BASE models
- **Service Discovery**: Consul registration and discovery
- **API Gateway**: Centralized routing and load balancing
- **Multi-Stage Builds**: Efficient Docker images
- **Database per Service**: Each service has its own database

## Troubleshooting

- **Services not registering**: Check Consul is running and network connectivity
- **Gateway routing fails**: Verify service names match Consul registration
- **Database connection errors**: Check environment variables and network
- **Build fails**: Verify Maven dependencies and Java version
