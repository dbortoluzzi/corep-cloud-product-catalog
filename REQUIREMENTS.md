# Project Requirements

This document lists all the requirements needed to run and develop this project across all phases.

## 📋 Overview

The project is divided into multiple phases, each with different requirements. Choose the requirements based on which phase you want to work on.

## 🔧 Core Requirements (All Phases)

### Essential

- **Java Development Kit (JDK)**: Version **17** or higher
  - Recommended: OpenJDK 17, Eclipse Temurin 17, or Oracle JDK 17
  - Verify: `java -version` should show version 17 or higher
  
- **Maven**: Version **3.6+** (or use Maven Wrapper included in project)
  - The project includes Maven Wrapper (`mvnw` / `mvnw.cmd`) - recommended
  - Verify: `./mvnw --version` or `mvn --version`
  
- **Git**: For cloning the repository
  - Verify: `git --version`

### Operating System

- **Linux** (recommended for cloud development)
- **macOS**
- **Windows** (with WSL2 recommended for Docker)

## 📦 Phase 1: Monolith Requirements

### Minimum Requirements

- ✅ **Java 17+** (JDK, not just JRE)
- ✅ **Maven 3.6+** or Maven Wrapper (included)
- ✅ **Git** (for cloning)

### Database (Development)

- **H2 Database**: Embedded, no installation needed
  - Included as Maven dependency
  - Automatically starts with the application

### Database (Production/Testing)

- **PostgreSQL**: Version **12+** (optional for local development)
  - Required for production profile
  - Can be installed locally or run in Docker

### Optional Tools

- **IDE**: IntelliJ IDEA, Eclipse, VS Code (with Java extensions)
- **Postman** or **curl**: For testing REST API endpoints
- **Browser**: For accessing Swagger UI (`http://localhost:8080/swagger-ui.html`)

### Native Build (Optional)

If you want to build native executables with GraalVM:

- **GraalVM**: Version **21.x** or **22.x**
  - Community Edition or Enterprise Edition
  - Download from: https://www.graalvm.org/
- **Native Image**: Component must be installed
  ```bash
  gu install native-image
  ```
- **GRAALVM_HOME**: Environment variable must be set
  ```bash
  export GRAALVM_HOME=/path/to/graalvm
  ```

## 🐳 Phase 2: Docker Requirements

### Additional Requirements

- ✅ **Docker**: Version **20.10+**
  - Verify: `docker --version`
  - Installation: https://docs.docker.com/get-docker/
  
- ✅ **Docker Compose**: Version **2.0+** (plugin, included with Docker Desktop)
  - Verify: `docker compose version`
  - Note: This project uses `docker compose` (v2), not `docker-compose` (v1)
  - Docker Compose v2 is included in Docker Desktop or can be installed as a plugin

### Docker Installation Options

**Linux**:
```bash
# Install Docker Engine
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose plugin
sudo apt-get update
sudo apt-get install docker-compose-plugin
```

**macOS / Windows**:
- Install Docker Desktop (includes Docker Compose v2)
- Download from: https://www.docker.com/products/docker-desktop

### System Requirements for Docker

- **RAM**: Minimum 4GB, recommended 8GB+
- **Disk Space**: At least 10GB free
- **CPU**: 64-bit processor with virtualization support

## 🔀 Phase 3: Microservices (Future)

### Additional Requirements

- All requirements from Phase 1 and Phase 2
- **Multiple Docker containers** capability
- **Network configuration** knowledge (optional)

## ☸️ Phase 4: Kubernetes (Future)

### Additional Requirements

- All requirements from Phase 1 and Phase 2
- **Kubernetes**: Version **1.24+** (for local: minikube, kind, or Docker Desktop Kubernetes)
- **kubectl**: Kubernetes command-line tool
- **Local Kubernetes cluster** (one of):
  - Minikube
  - kind (Kubernetes in Docker)
  - Docker Desktop with Kubernetes enabled
  - Cloud provider (GKE, EKS, AKS)

## 📊 Summary Table

| Phase | Java | Maven | Docker | Docker Compose | PostgreSQL | GraalVM |
|-------|------|-------|--------|----------------|------------|---------|
| **1. Monolith** | ✅ 17+ | ✅ 3.6+ | ❌ | ❌ | ⚠️ Optional | ⚠️ Optional |
| **2. Docker** | ✅ 17+ | ✅ 3.6+ | ✅ 20.10+ | ✅ 2.0+ | ⚠️ Optional | ❌ |
| **3. Microservices** | ✅ 17+ | ✅ 3.6+ | ✅ 20.10+ | ✅ 2.0+ | ✅ Required | ❌ |
| **4. Kubernetes** | ✅ 17+ | ✅ 3.6+ | ✅ 20.10+ | ✅ 2.0+ | ✅ Required | ❌ |

**Legend**:
- ✅ Required
- ⚠️ Optional (but recommended)
- ❌ Not needed

## 🔍 Verification Commands

Verify your installation:

```bash
# Java
java -version
# Should show: openjdk version "17" or higher

# Maven (if installed globally)
mvn --version
# Or use Maven Wrapper
./mvnw --version

# Docker
docker --version
# Should show: Docker version 20.10 or higher

# Docker Compose v2
docker compose version
# Should show: Docker Compose version v2.x.x

# Git
git --version
```

## 🚀 Quick Start Check

For **Phase 1** (Monolith), you only need:
1. ✅ Java 17+
2. ✅ Maven Wrapper (included) or Maven 3.6+
3. ✅ Git

For **Phase 2** (Docker), you additionally need:
4. ✅ Docker 20.10+
5. ✅ Docker Compose v2

## 📚 Additional Resources

- **Java 17 Installation**: https://adoptium.net/
- **Maven Installation**: https://maven.apache.org/install.html
- **Docker Installation**: https://docs.docker.com/get-docker/
- **Docker Compose v2**: https://docs.docker.com/compose/
- **GraalVM**: https://www.graalvm.org/

## ❓ Troubleshooting

### Java Version Issues

If you have multiple Java versions:
```bash
# Linux/macOS: Use update-alternatives or jenv
# Windows: Set JAVA_HOME environment variable

# Verify which Java is used
which java
java -version
```

### Maven Wrapper Issues

If `./mvnw` doesn't work:
```bash
# Make it executable (Linux/macOS)
chmod +x mvnw

# Or use global Maven
mvn clean install
```

### Docker Issues

If Docker commands require `sudo`:
```bash
# Add user to docker group (Linux)
sudo usermod -aG docker $USER
# Log out and log back in
```

### Docker Compose v1 vs v2

If you have v1 (`docker-compose` with hyphen):
- The commands will still work, but v2 (`docker compose` with space) is recommended
- Consider upgrading to Docker Desktop or installing Docker Compose plugin

---

**Last Updated**: 2025
**Project**: Product Catalog Management System
**Course**: Master di I Livello in Cloud Computing - Università degli Studi di Torino - COREP

