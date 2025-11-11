# Corep Cloud Product Catalog

**Product Catalog Management System** - Cloud Computing Course Spike Project

This repository demonstrates the evolution of a Spring Boot application from a local monolith to cloud-native microservices, passing through Docker containerization and Kubernetes orchestration.

## 📁 Repository Structure

This repository is organized by phases to show the evolution step-by-step:

```
corep-cloud-product-catalog/
├── 01-monolith/          # Phase 1: Monolith (Current)
│   ├── README.md         # Phase 1 documentation
│   ├── pom.xml           # Maven configuration
│   └── src/              # Source code
│
├── 02-docker/            # Phase 2: Docker (Future)
│   └── README.md         # Coming soon...
│
├── 03-microservices/     # Phase 3: Microservices (Future)
│   └── README.md         # Coming soon...
│
└── 04-kubernetes/        # Phase 4: Kubernetes (Future)
    └── README.md         # Coming soon...
```

## 🗺️ Project Phases

### ✅ Phase 1: Monolith (Current)
**Location**: [`01-monolith/`](./01-monolith/)

- Complete CRUD with Spring Boot
- Modular monolith architecture (catalog + inventory domains)
- Advanced Spring Boot features:
  - Custom queries (JPQL, Native SQL)
  - Pagination and sorting
  - Transaction management
  - Validation
  - Exception handling
  - Spring Profiles
  - Spring Boot Actuator
  - OpenAPI/Swagger documentation
- Comprehensive testing (unit, integration, repository tests)

**See**: [`01-monolith/README.md`](./01-monolith/README.md) for detailed documentation.

### 🔜 Phase 2: Docker
**Location**: [`02-docker/`](./02-docker/)

- Dockerfile for containerization
- Docker Compose for local orchestration
- PostgreSQL as persistent database
- Environment-based configuration

### 🔜 Phase 3: Microservices
**Location**: [`03-microservices/`](./03-microservices/)

- Decompose monolith into microservices:
  - Product Catalog Service
  - Inventory Service
- Docker Compose for multi-service orchestration
- Service-to-service communication
- Shared libraries

### 🔜 Phase 4: Kubernetes
**Location**: [`04-kubernetes/`](./04-kubernetes/)

- Kubernetes deployment manifests
- Service definitions
- ConfigMaps and Secrets
- Health checks and readiness probes
- Horizontal Pod Autoscaling

## 🚀 Quick Start

### Phase 1: Monolith

```bash
cd 01-monolith
mvn clean install
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

For detailed instructions, see [`01-monolith/README.md`](./01-monolith/README.md)

## 📚 Documentation

- **[ROADMAP.md](./ROADMAP.md)** - Detailed project evolution path from monolith to cloud-native microservices
- **[USE_CASE.md](./USE_CASE.md)** - Business requirements, domain model, workflows, and architecture evolution

## 🎯 Learning Objectives

This project serves as a practical example for the Cloud Computing course, demonstrating:

1. **Spring Boot Development**
   - RESTful API design
   - Data persistence with JPA
   - Advanced Spring Boot features

2. **Containerization**
   - Docker fundamentals
   - Multi-container applications
   - Container orchestration

3. **Cloud Deployment**
   - Kubernetes basics
   - Service discovery
   - Configuration management
   - Health monitoring

4. **Microservices Architecture**
   - Service decomposition
   - Inter-service communication
   - Distributed system patterns

## 🏗️ Architecture Evolution

```
Phase 1: Monolith
┌─────────────────────────┐
│  Product Catalog App    │
│  (Catalog + Inventory)  │
└─────────────────────────┘

Phase 2: Containerized Monolith
┌─────────────────────────┐
│  Docker Container       │
│  ┌───────────────────┐  │
│  │  Spring Boot App   │  │
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │  PostgreSQL        │  │
│  └───────────────────┘  │
└─────────────────────────┘

Phase 3: Microservices
┌──────────────┐  ┌──────────────┐
│ Catalog Svc  │  │ Inventory Svc│
└──────────────┘  └──────────────┘
       │                 │
       └────────┬────────┘
                │
         ┌──────▼──────┐
         │  PostgreSQL  │
         └─────────────┘

Phase 4: Kubernetes
┌─────────────────────────────────┐
│     Kubernetes Cluster          │
│  ┌──────────┐    ┌──────────┐  │
│  │ Catalog  │    │Inventory │  │
│  │   Pod    │    │   Pod    │  │
│  └──────────┘    └──────────┘  │
│         │              │        │
│    ┌────▼──────────────▼──┐    │
│    │   Service (SVC)       │    │
│    └──────────┬────────────┘    │
│         ┌─────▼─────┐            │
│         │ PostgreSQL│            │
│         │ StatefulSet│           │
│         └───────────┘            │
└─────────────────────────────────┘
```

## 👥 Author

Project developed for **Corep - Cloud Computing Course**

## 📄 License

This project is for educational purposes.
