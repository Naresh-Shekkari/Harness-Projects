# Project 1: Core Banking Microservice - Basic CI Pipeline

> **Domain**: Retail Banking — User Authentication Service  
> **Tech Stack**: Java Spring Boot, Maven, Docker, Harness CI, DockerHub, GitHub  
> **Objective**: Build an end-to-end automated Continuous Integration (CI) pipeline that triggers on git commits, compiles Java source code, executes unit tests, creates an immutable Docker image, and publishes it to DockerHub.

---

## 🎯 Architecture Diagram

```
[ Developer ] ──► git push ──► [ GitHub Repo ]
                                     │ (Webhook Trigger)
                                     ▼
                              [ Harness CI ]
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         ▼                           ▼                           ▼
  1. Clone Code              2. Run Maven Test           3. Build Docker Image
  (Git Connector)            (JDK 17 Container)          (Kaniko / BuildKit)
                                                                 │
                                                                 ▼
                                                        4. Push to DockerHub
                                                        (Secrets & Registry)
```

---

## 📦 1. Application Code & Structure

Below is the lightweight Spring Boot User Authentication Microservice structure created under `projects/01-core-banking-ci/`:

```
projects/01-core-banking-ci/
├── pom.xml
├── Dockerfile
├── harness-ci-pipeline.yaml
└── src/
    ├── main/java/com/bank/auth/
    │   ├── AuthApplication.java
    │   └── AuthController.java
    └── test/java/com/bank/auth/
        └── AuthControllerTest.java
```

---

## 🛠️ 2. Key Component Breakdown

### A. Spring Boot Configuration (`pom.xml`)
- Java 17 target.
- Includes `spring-boot-starter-web` and `spring-boot-starter-test` (JUnit 5 + Mockito).

### B. Containerization (`Dockerfile`)
- Multi-stage build for minimal container footprint:
  - **Stage 1 (Build)**: Maven + JDK 17 to compile `.jar` file.
  - **Stage 2 (Runtime)**: Eclipse Temurin JRE 17 lightweight image with non-root security user.

### C. Harness CI Pipeline (`harness-ci-pipeline.yaml`)
- **Git Trigger**: Fires on commits to `main` branch.
- **Stage 1: Code Compilation & Unit Testing**:
  - Uses standard Maven container image (`maven:3.8.6-openjdk-17-slim`).
  - Command: `mvn clean test package`.
- **Stage 2: Build & Push Docker Image**:
  - Uses Harness `BuildAndPushDockerRegistry` step (Kaniko engine under the hood — daemonless Docker build).
  - Pushes tag `auth-service:<+pipeline.sequenceId>` and `auth-service:latest`.
  - Credentials securely injected via Harness Secrets Manager (`<+secrets.getValue("dockerhub_password")>`).

---

## 🚀 3. Step-by-Step Hands-On Instructions

### Step 1: Create Harness Connector for GitHub
1. In Harness UI $\rightarrow$ **Connectors** $\rightarrow$ **New Connector** $\rightarrow$ **GitHub**.
2. Select **Account / Repository level**.
3. Authentication: Personal Access Token (PAT) stored as Harness Secret (`github_pat`).

### Step 2: Create Harness Connector for DockerHub
1. In Harness UI $\rightarrow$ **Connectors** $\rightarrow$ **New Connector** $\rightarrow$ **Docker Registry**.
2. URL: `https://index.docker.io/v1/`.
3. Credentials: Your DockerHub Username + Access Token (`dockerhub_password`).

### Step 3: Import & Run the Pipeline
1. Create a new Pipeline in Harness CI: **Core_Banking_Auth_CI**.
2. Paste the provided `harness-ci-pipeline.yaml`.
3. Click **Run** or push a commit to your GitHub repository!

---

## 📖 4. Deep-Dive: Why Kaniko for Harness Docker Builds?
Traditional Docker builds require mounting `/var/run/docker.sock` into the container worker, which poses a severe security risk in shared Kubernetes clusters (Docker-in-Docker / DinD vulnerability).

Harness uses **Kaniko** by default:
- Builds container images inside an unprivileged Kubernetes pod.
- Doesn't rely on a running Docker daemon.
- Completely safe for multi-tenant enterprise Kubernetes environments.
