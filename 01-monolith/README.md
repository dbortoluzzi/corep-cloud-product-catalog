# Phase 1: Monolith

**Product Catalog Management System** - Spring Boot Monolith Implementation

This is the first phase of the project, implementing a complete CRUD application with Spring Boot as a modular monolith.

## 📋 Description

This phase implements a Spring Boot application with a modular monolith architecture. The application manages a product catalog and inventory, with clear domain boundaries that will facilitate future decomposition into microservices.

**Key Features**:
- Product catalog management (CRUD operations)
- Inventory management with stock reservations
- Modular architecture ready for microservices extraction
- Cloud-ready configuration

## 🎯 Project Objectives

- Implement a complete CRUD with Spring Boot
- Use Spring Data JPA for data persistence
- Implement validation and error handling
- Prepare the application for cloud deployment
- Use Spring Boot Actuator for monitoring
- Demonstrate advanced Spring Boot features (custom queries, pagination, transactions)

## 🏗️ Architecture

The project follows a 3-layer architecture:

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (H2/PostgreSQL)
```

## 📦 Technologies Used

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (development)
- **PostgreSQL** (production/cloud)
- **Spring Boot Actuator**
- **Lombok**
- **Maven**
- **JUnit 5** (testing)
- **SpringDoc OpenAPI** (Swagger)

## 🚀 Prerequisites

- Java 17 or higher
- Maven 3.6+
- (Optional) Docker for containerization

## 📥 Installation and Setup

### 1. Navigate to the monolith directory

```bash
cd 01-monolith
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

#### Development Profile (default - uses H2 in-memory database)

```bash
mvn spring-boot:run
```

Or explicitly:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

#### Production Profile (uses PostgreSQL)

First, ensure PostgreSQL is running, then:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

Or set environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/productdb
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=password
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🔧 Spring Profiles

Spring Boot profiles allow you to configure the application for different environments. The `application.yml` file contains multiple profile configurations separated by `---`.

### How Profiles Work

1. **Default Configuration**: The top section of `application.yml` contains the default configuration
2. **Profile-Specific Configuration**: Sections marked with `spring.config.activate.on-profile` override defaults for that profile
3. **Profile Activation**: Profiles are activated via:
   - Command line: `--spring.profiles.active=dev`
   - Environment variable: `SPRING_PROFILES_ACTIVE=prod`
   - `application.yml`: `spring.profiles.active: dev`

### Available Profiles

#### `dev` Profile (Development)
- Uses H2 in-memory database
- SQL logging enabled
- H2 console enabled at `/h2-console`
- Auto-creates/drops schema on startup

**Activate:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

#### `prod` Profile (Production)
- Uses PostgreSQL database
- SQL logging disabled
- Schema updates (doesn't drop data)
- Uses environment variables for configuration

**Activate:**
```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/productdb
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=password
mvn spring-boot:run
```

#### `test` Profile (Testing)
- Used automatically during tests
- Uses H2 in-memory database
- Optimized for test execution

## 🔌 API Endpoints

### Product Catalog Endpoints

**Base URL**: `http://localhost:8080/api/v1/products`

| Method | Endpoint | Description | Query Type |
|--------|----------|-------------|------------|
| GET | `/api/v1/products` | List all products | Derived Query |
| GET | `/api/v1/products?category={cat}` | Filter products by category | Derived Query |
| GET | `/api/v1/products?page=0&size=10&sort=price,asc` | Paginated products | Pagination |
| GET | `/api/v1/products/{id}` | Get product by ID | Derived Query |
| POST | `/api/v1/products` | Create a new product | - |
| PUT | `/api/v1/products/{id}` | Update an existing product | - |
| DELETE | `/api/v1/products/{id}` | Delete a product | - |
| GET | `/api/v1/products/category/{category}/ordered-by-price` | Products by category ordered by price | JPQL Query |
| GET | `/api/v1/products/statistics/categories` | Category statistics | Native SQL Query |
| GET | `/api/v1/products/price-range?minPrice=50&maxPrice=200` | Products by price range | Native SQL Query |

### Inventory Endpoints

**Base URL**: `http://localhost:8080/api/v1/inventory`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/inventory/product/{productId}?initialStock={qty}` | Create inventory for product |
| GET | `/api/v1/inventory/product/{productId}` | Get inventory for product |
| PUT | `/api/v1/inventory/product/{productId}/stock?stockQuantity={qty}` | Update stock quantity |
| POST | `/api/v1/inventory/product/{productId}/reserve?quantity={qty}` | Reserve stock |
| POST | `/api/v1/inventory/product/{productId}/release?quantity={qty}` | Release reserved stock |
| GET | `/api/v1/inventory/low-stock?threshold=10` | Get low stock items |
| GET | `/api/v1/inventory/out-of-stock` | Get out of stock items |
| DELETE | `/api/v1/inventory/product/{productId}` | Delete inventory |

### Query Types Explained

1. **Derived Queries**: Spring Data JPA automatically generates queries from method names
   - Example: `findByCategory(String category)`

2. **JPQL Queries**: Java Persistence Query Language - database-agnostic
   - Example: `@Query("SELECT p FROM Product p WHERE p.price >= :minPrice")`

3. **Native SQL Queries**: Raw SQL queries for complex operations
   - Example: `@Query(value = "SELECT * FROM products WHERE stock_quantity < :threshold", nativeQuery = true)`

### Usage Examples

#### Create a product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 15",
    "description": "High-performance laptop with 4K display",
    "price": 1299.99,
    "category": "Electronics",
    "stockQuantity": 25
  }'
```

#### Get all products

```bash
curl http://localhost:8080/api/v1/products
```

#### Get products with pagination

```bash
curl "http://localhost:8080/api/v1/products?page=0&size=5&sort=price,asc"
```

#### Get product by ID

```bash
curl http://localhost:8080/api/v1/products/1
```

#### Filter by category

```bash
curl http://localhost:8080/api/v1/products?category=Electronics
```

#### Get low stock products (Inventory endpoint)

```bash
curl "http://localhost:8080/api/v1/inventory/low-stock?threshold=10"
```

#### Get category statistics (Native SQL with Aggregation)

```bash
curl http://localhost:8080/api/v1/products/statistics/categories
```

## 🏥 Health Checks

The application exposes monitoring endpoints via Spring Boot Actuator:

- **Health Check**: `http://localhost:8080/actuator/health`
- **Info**: `http://localhost:8080/actuator/info`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Liveness Probe**: `http://localhost:8080/actuator/health/liveness`
- **Readiness Probe**: `http://localhost:8080/actuator/health/readiness`

## 📚 API Documentation (Swagger/OpenAPI)

Interactive API documentation is available via Swagger UI:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

The Swagger UI provides:
- Complete API documentation
- Interactive endpoint testing
- Request/response schemas
- Example requests

## 🗄️ Database

### Development (H2)

In development mode, the application uses H2 in-memory database. The console interface is available at:

```
http://localhost:8080/h2-console
```

**JDBC URL**: `jdbc:h2:mem:productdb`  
**Username**: `sa`  
**Password**: (empty)

### Production (PostgreSQL)

For production, configure environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/productdb
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_PROFILES_ACTIVE=prod
```

## 🧪 Testing

### Run all tests

```bash
mvn test
```

### Run specific test class

```bash
mvn test -Dtest=ProductServiceTest
```

### Test Types

1. **Unit Tests** (`ProductServiceTest`): Test service layer with mocked repository
2. **Repository Tests** (`ProductRepositoryTest`): Test data access layer with in-memory database
3. **Controller Tests** (`ProductControllerTest`): Test REST endpoints with MockMvc
4. **Integration Tests** (`ProductIntegrationTest`): Full-stack tests with `@SpringBootTest`

### Test Coverage

The project includes:
- Service layer unit tests
- Repository integration tests
- Controller endpoint tests
- Full integration tests (end-to-end)
- Validation tests

## 📁 Project Structure (Modular Monolith)

The project is organized with clear domain boundaries to facilitate future decomposition into microservices:

```
src/
├── main/
│   ├── java/
│   │   └── com/corep/productcatalog/
│   │       ├── ProductCatalogApplication.java
│   │       │
│   │       ├── catalog/              # Product Catalog Domain
│   │       │   ├── controller/
│   │       │   │   └── ProductController.java
│   │       │   ├── service/
│   │       │   │   └── ProductService.java
│   │       │   ├── repository/
│   │       │   │   └── ProductRepository.java
│   │       │   ├── entity/
│   │       │   │   └── Product.java
│   │       │   ├── dto/
│   │       │   │   └── ProductDTO.java
│   │       │   └── exception/
│   │       │       └── ProductNotFoundException.java
│   │       │
│   │       ├── inventory/           # Inventory Domain
│   │       │   ├── controller/
│   │       │   │   └── InventoryController.java
│   │       │   ├── service/
│   │       │   │   └── InventoryService.java
│   │       │   ├── repository/
│   │       │   │   └── InventoryRepository.java
│   │       │   ├── entity/
│   │       │   │   └── Inventory.java
│   │       │   ├── dto/
│   │       │   │   └── InventoryDTO.java
│   │       │   └── exception/
│   │       │       ├── InventoryNotFoundException.java
│   │       │       └── InsufficientStockException.java
│   │       │
│   │       └── shared/               # Shared Components
│   │           ├── exception/
│   │           │   └── GlobalExceptionHandler.java
│   │           └── config/
│   │               ├── ApplicationProperties.java
│   │               ├── DataInitializer.java
│   │               └── OpenApiConfig.java
│   │
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── com/corep/productcatalog/
            ├── catalog/
            │   ├── controller/
            │   ├── service/
            │   └── repository/
            ├── inventory/
            └── integration/
```

## 🎓 Spring Boot Features Demonstrated

### 1. **Spring Data JPA Query Methods**
- Derived queries from method names
- Example: `findByCategory(String category)`

### 2. **Custom Queries**
- **JPQL Queries**: Database-agnostic queries using entity names
- **Native SQL Queries**: Raw SQL for complex operations
- Example: `@Query("SELECT p FROM Product p WHERE p.price >= :minPrice")`

### 3. **Pagination and Sorting**
- `Pageable` interface for pagination
- `Sort` for ordering results
- Example: `Page<Product> findByCategory(String category, Pageable pageable)`

### 4. **Transaction Management**
- `@Transactional` for method-level transactions
- `@Transactional(readOnly = true)` for read-only operations
- Automatic rollback on exceptions

### 5. **Validation**
- Bean Validation annotations (`@NotNull`, `@NotBlank`, `@Size`, etc.)
- Custom validation messages
- Automatic validation in controllers

### 6. **Exception Handling**
- `@RestControllerAdvice` for global exception handling
- Custom exception classes
- Standardized error responses

### 7. **Spring Profiles**
- Environment-specific configurations
- Profile activation via command line or environment variables

### 8. **Spring Boot Actuator**
- Health checks
- Metrics endpoint
- Liveness and readiness probes

### 9. **Configuration Properties**
- `@ConfigurationProperties` for type-safe configuration
- Externalized configuration management
- Useful for cloud deployments with config servers

### 10. **API Documentation**
- **Swagger/OpenAPI** integration
- Interactive API documentation
- Auto-generated from code annotations

### 11. **Data Initialization**
- `CommandLineRunner` for application startup tasks
- Profile-based initialization (dev profile only)
- Sample data population

### 12. **Integration Testing**
- `@SpringBootTest` for full application context testing
- End-to-end API testing
- Database integration tests

## 🏗️ Modular Architecture

This project is structured as a **modular monolith** with clear domain boundaries:

- **catalog/** - Product Catalog Domain (will become Product Catalog Service)
- **inventory/** - Inventory Domain (will become Inventory Service)
- **shared/** - Shared components (exception handling, configuration)

This structure makes it easy to:
1. Understand domain boundaries
2. Test each domain independently
3. Extract services when needed
4. Scale services independently

## 📚 Additional Documentation

- **[USE_CASE.md](../../USE_CASE.md)** - Business requirements, domain model, workflows, and architecture evolution
- **[ROADMAP.md](../../ROADMAP.md)** - Detailed project evolution path from monolith to cloud-native microservices
- **[CIRCULAR_DEPENDENCY_SOLUTIONS.md](./CIRCULAR_DEPENDENCY_SOLUTIONS.md)** - Solutions for circular dependencies in modular monoliths (refactoring, events, @Lazy, etc.)

## 🗺️ Roadmap

- [x] **Phase 1: Monolith** - Base CRUD with Spring Boot (Current)
  - [x] Advanced Spring Boot features (queries, pagination, transactions)
  - [x] Modular monolith structure (catalog + inventory domains)
  - [x] Testing (unit, integration, repository tests)
- [ ] **Phase 2: Docker - Monolith** - Containerize the monolith
- [ ] **Phase 3: Microservices + Docker Compose** - Decompose into services
- [ ] **Phase 4: Kubernetes - Microservices** - Deploy services to K8s

For detailed roadmap information, see **[ROADMAP.md](./ROADMAP.md)**

## 👥 Author

Project developed for Cloud Computing Course - Corep

## 📄 License

This project is for educational purposes.

