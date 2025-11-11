# Project Roadmap

This document describes the evolution path of the Product Catalog project through different phases, from a local monolith to a cloud-native microservices architecture.

## 🎯 Learning Path

The project is designed to be developed incrementally, with each phase building upon the previous one. This approach allows students to:

1. Understand each technology in isolation
2. See the evolution of the architecture
3. Learn best practices step by step
4. Apply concepts in a realistic scenario

## 📋 Phases Overview

```
Phase 1: Monolith (Current)
    ↓
Phase 2: Docker - Monolith
    ↓
Phase 3: Microservices + Docker Compose
    ↓
Phase 4: Kubernetes - Microservices
```

## 🏗️ Phase 1: Monolith (Current Phase)

**Status**: ✅ Complete

**Description**: 
A Spring Boot application implementing a modular monolith with clear domain boundaries (catalog and inventory domains).

**Key Features**:
- Complete CRUD operations for products and inventory
- Modular architecture (catalog, inventory, shared packages)
- Spring Boot profiles (dev, prod, test)
- Advanced Spring Boot features (custom queries, pagination, transactions)
- Comprehensive testing (unit, integration, repository tests)
- API documentation (Swagger/OpenAPI)
- Health checks (Actuator)

**Technologies**:
- Spring Boot 3.2.0
- Spring Data JPA
- H2 (dev) / PostgreSQL (prod)
- Maven
- JUnit 5

**Learning Objectives**:
- Spring Boot fundamentals
- REST API design
- Data persistence with JPA
- Modular monolith architecture
- Testing strategies

---

## 🐳 Phase 2: Docker - Monolith

**Status**: ⏳ Planned

**Description**: 
Containerize the monolith application using Docker. This phase focuses on learning Docker fundamentals without changing the application architecture.

**What Will Be Added**:
- `Dockerfile` for the monolith application
- `docker-compose.yml` for local development
- PostgreSQL container configuration
- Multi-stage builds for optimization
- Docker networking basics

**Structure**:
```
project-root/
├── Dockerfile
├── docker-compose.yml
├── src/                    # Same as Phase 1
└── README.md              # Docker-specific instructions
```

**Learning Objectives**:
- Docker containerization
- Dockerfile best practices
- Docker Compose for multi-container applications
- Container networking
- Image optimization

**Key Commands**:
```bash
# Build image
docker build -t product-catalog:1.0 .

# Run container
docker run -p 8080:8080 product-catalog:1.0

# Use docker-compose
docker-compose up
```

---

## 🔀 Phase 3: Microservices + Docker Compose

**Status**: ⏳ Planned

**Description**: 
Decompose the monolith into separate microservices. Each service will be containerized and orchestrated using Docker Compose. This phase focuses on the architectural decomposition without Kubernetes complexity.

**What Will Change**:
- **Catalog Service**: Extracted from `catalog/` package
  - Own `Dockerfile`
  - Own Spring Boot application
  - Own database schema (products table)
  
- **Inventory Service**: Extracted from `inventory/` package
  - Own `Dockerfile`
  - Own Spring Boot application
  - Own database schema (inventory table)
  - Communicates with Catalog Service via REST

- **Docker Compose**: Orchestrates all services
  - Catalog Service container
  - Inventory Service container
  - PostgreSQL container (shared or separate)
  - Network configuration
  - Environment variables

**Structure**:
```
project-root/
├── catalog-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── inventory-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── docker-compose.yml
└── README.md              # Microservices architecture explanation
```

**Learning Objectives**:
- Microservices decomposition strategies
- Service-to-service communication (REST)
- Database per service pattern
- Docker Compose for microservices
- Service boundaries and contracts
- API design for inter-service communication

**Key Concepts**:
- Service independence
- API contracts
- Data consistency challenges
- Service discovery (optional)
- Circuit breakers (optional)

---

## ☸️ Phase 4: Kubernetes - Microservices

**Status**: ⏳ Planned

**Description**: 
Deploy the microservices architecture on Kubernetes. This phase uses the same services from Phase 3 but replaces Docker Compose with Kubernetes orchestration.

**What Will Be Added**:
- Kubernetes manifests for each service:
  - `Deployment` for Catalog Service
  - `Deployment` for Inventory Service
  - `Service` resources for internal communication
  - `ConfigMap` for configuration
  - `Secret` for sensitive data
  - `Ingress` for external access
- Helm charts (optional, for advanced topics)
- Kubernetes networking
- Resource limits and requests
- Health checks (liveness/readiness probes)

**Structure**:
```
project-root/
├── catalog-service/
│   ├── Dockerfile
│   ├── pom.xml
│   ├── src/
│   └── k8s/
│       ├── deployment.yaml
│       ├── service.yaml
│       └── configmap.yaml
├── inventory-service/
│   ├── Dockerfile
│   ├── pom.xml
│   ├── src/
│   └── k8s/
│       ├── deployment.yaml
│       ├── service.yaml
│       └── configmap.yaml
├── k8s/
│   ├── namespace.yaml
│   ├── ingress.yaml
│   └── postgresql/
│       ├── deployment.yaml
│       └── service.yaml
└── README.md              # Kubernetes deployment guide
```

**Learning Objectives**:
- Kubernetes fundamentals
- Pods, Deployments, Services
- ConfigMaps and Secrets
- Ingress controllers
- Resource management
- Health checks and probes
- Scaling strategies

**Key Commands**:
```bash
# Deploy services
kubectl apply -f k8s/

# Check status
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/catalog-service
```

---

## 🔄 Evolution Summary

| Phase | Architecture | Containerization | Orchestration | Database |
|-------|-------------|------------------|---------------|----------|
| **1. Monolith** | Single app | None | None | H2/PostgreSQL (local) |
| **2. Docker** | Single app | Docker | Docker Compose | PostgreSQL (container) |
| **3. Microservices** | 2 services | Docker | Docker Compose | PostgreSQL (container) |
| **4. Kubernetes** | 2 services | Docker | Kubernetes | PostgreSQL (K8s) |

## 🎓 Why This Order?

1. **Phase 1 → Phase 2**: Learn Docker with a simple, known application
2. **Phase 2 → Phase 3**: Decompose architecture while keeping orchestration simple (Docker Compose)
3. **Phase 3 → Phase 4**: Change only the orchestration layer (Docker Compose → Kubernetes), keeping the same services

This approach separates concerns:
- **Architecture** (monolith vs microservices) is learned in Phase 3
- **Orchestration** (Docker Compose vs Kubernetes) is learned in Phase 4

## 📚 Additional Resources

Each phase will include:
- Detailed README with setup instructions
- Code examples
- Common pitfalls and solutions
- Best practices
- Troubleshooting guide

## 🚀 Next Steps

1. Complete Phase 1 (✅ Done)
2. Start Phase 2: Add Docker support to the monolith
3. Continue with Phase 3: Decompose into microservices
4. Finish with Phase 4: Deploy to Kubernetes

