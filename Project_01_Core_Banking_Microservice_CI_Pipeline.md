# Project 1: Core Banking Microservice — Basic CI Pipeline

> **Domain**: Retail Banking — User Authentication Service  
> **Tech Stack**: Java Spring Boot, Maven, Docker, Harness CI, DockerHub, GitHub  
> **Objective**: Learn end-to-end Continuous Integration (CI) by first executing all development and deployment tasks **manually** in the terminal, and then converting those manual tasks into an **automated enterprise DevOps CI pipeline**.

---

## 👥 Role Responsibility Breakdown: Developer vs DevOps

In a modern enterprise engineering team, responsibilities are clearly demarcated between Developers and DevOps Engineers:

| Task / Activity | Primary Role | Description |
| :--- | :--- | :--- |
| **Microservice Business Logic** | 👨‍💻 **Developer Task** | Writing Spring Boot controllers, application code, and core banking features. |
| **Unit & Integration Tests** | 👨‍💻 **Developer Task** | Writing JUnit / MockMvc test cases to validate business logic correctness. |
| **Build Configuration (`pom.xml`)** | 👨‍💻 **Developer Task** | Managing Java dependencies, plugins, and build targets. |
| **Containerization (`Dockerfile`)** | ⚙️ **DevOps Task** | Designing secure multi-stage Docker builds, non-root users, and minimal runtime images. |
| **Local Container Testing** | ⚙️ **DevOps Task** | Running containers locally to verify port bindings, environment variables, and health checks. |
| **Registry Management & Tagging** | ⚙️ **DevOps Task** | Managing container repositories, tagging standards, and access control. |
| **CI/CD Pipeline Automation** | ⚙️ **DevOps Task** | Creating pipeline-as-code YAMLs, setting up Git/Registry connectors, secrets, and webhooks. |
| **Infrastructure & Build Runners** | ⚙️ **DevOps Task** | Provisioning and managing build delegates, Kubernetes worker clusters, and runner environments. |

---

## 🛠️ PART 1: Manual Execution Guide (Perform Manually in Terminal First)

Before writing any automation scripts or pipelines, a DevOps engineer must understand the **exact manual commands** required to compile, test, containerize, and publish the microservice.

```
[ 1. Write Code ] ──► [ 2. mvn clean test ] ──► [ 3. docker build ] ──► [ 4. docker run ] ──► [ 5. Test APIs ] ──► [ 6. docker push ]
 (Developer Task)        (Developer Task)          (DevOps Task)           (DevOps Task)         (DevOps Task)        (DevOps Task)
```

---

### Step 1: Navigate to Project Directory & Review Code
> **Role Tag**: 👨‍💻 **DEVELOPER TASK**

Open PowerShell or terminal and navigate to the project directory:

```powershell
cd C:\Users\Naresh\Documents\GitHub\AGY\projects\01-core-banking-ci
```

*Project Directory Structure:*
```text
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

### Step 2: Compile & Run Unit Tests Manually
> **Role Tag**: 👨‍💻 **DEVELOPER TASK**

Run the Maven build command manually in your terminal to verify that the source code compiles and all unit tests pass:

```powershell
mvn clean test
```

**What Happens Manually**:
- Maven downloads Java dependencies.
- Compiles Java source files under `src/main/java/`.
- Executes JUnit tests under `src/test/java/`.
- Generates test reports in `target/surefire-reports/`.
- **Expected Output**: `BUILD SUCCESS` with 3 test cases passed.

---

### Step 3: Create Container Blueprint (`Dockerfile`)
> **Role Tag**: ⚙️ **DEVOPS TASK**

Create/review the multi-stage `Dockerfile` to package the application securely.

```dockerfile
# STAGE 1: Build & Package Java Application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# STAGE 2: Lightweight Security-Hardened Runtime Environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
COPY --from=builder /app/target/auth-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**DevOps Best Practices Enforced Here**:
- **Multi-Stage Build**: Keeps build tools (Maven, JDK) out of the final production image.
- **Security Hardening**: Runs as non-root `appuser` to prevent container escape exploits.
- **Minimal Base Image**: Uses Alpine JRE to reduce attack surface and image size (~180MB vs ~600MB).

---

### Step 4: Build Docker Image Manually
> **Role Tag**: ⚙️ **DEVOPS TASK**

Execute the Docker build command manually to package the application into a container image:

```powershell
docker build -t auth-service:1.0 .
```

*Verify Built Image*:
```powershell
docker images auth-service:1.0
```

---

### Step 5: Run Container & Test REST Endpoints Manually
> **Role Tag**: ⚙️ **DEVOPS TASK**

Run the built image locally and test the REST endpoints manually:

```powershell
# 1. Start container detached on port 8080
docker run -d -p 8080:8080 --name auth-service-manual auth-service:1.0

# 2. Test Health Endpoint (PowerShell)
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/health" -Method Get | ConvertTo-Json

# 3. Test Successful Login (PowerShell)
$body = @{username="bankuser"; password="securepass123"} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -ContentType "application/json" -Body $body | ConvertTo-Json

# 4. Clean up test container
docker rm -f auth-service-manual
```

---

### Step 6: Authenticate, Tag & Push to Container Registry Manually
> **Role Tag**: ⚙️ **DEVOPS TASK**

Manually log into DockerHub, tag the image with your registry repository name, and push it:

```powershell
# 1. Login to Registry (Requires interactive password entry)
docker login

# 2. Tag image with remote repository path and version (Replace <YOUR_DOCKERHUB_USERNAME>)
docker tag auth-service:1.0 <YOUR_DOCKERHUB_USERNAME>/auth-service:1.0
docker tag auth-service:1.0 <YOUR_DOCKERHUB_USERNAME>/auth-service:latest

# 3. Push container image to remote registry
docker push <YOUR_DOCKERHUB_USERNAME>/auth-service:1.0
docker push <YOUR_DOCKERHUB_USERNAME>/auth-service:latest
```

---

## ⚡ PART 2: DevOps Automation Guide (Moving Manual Tasks to CI Pipeline)

Executing commands manually in a terminal has major limitations in enterprise environments:
- ❌ Requires developer/DevOps manual intervention for every code change.
- ❌ Risk of credential leaks when logging into registries manually.
- ❌ Inconsistent build environments ("Works on my machine" syndrome).
- ❌ No automated audit log or test reporting dashboard.

Here is how every manual task maps to an **Automated Harness CI Pipeline Step**:

---

### 🔄 Manual vs Automated CI Pipeline Mapping

| Manual Activity | Automated DevOps Pipeline Step | Tool / Technology Used | Why / Benefit of Automation |
| :--- | :--- | :--- | :--- |
| **Git Push / Manual Trigger** | `Webhook Trigger` | GitHub Webhooks + Harness Git Connector | Pipeline executes automatically on every `git push`. No human trigger needed. |
| **Local Machine Setup** | `Infrastructure Execution Environment` | Harness Delegate + K8s / Docker Runner | Isolated, reproducible containerized runner per build. |
| **`mvn clean test`** | `Run` Step (`Maven_Build_and_Test`) | Maven Docker Container (`maven:3.9.6`) | Standardized JDK build runner; outputs JUnit test results directly to Harness UI. |
| **`docker login`** | `Harness Secrets Manager` + `Connector` | Harness Encrypted Secrets (`dockerhub_password`) | Zero credentials stored on local machines or exposed in logs. |
| **`docker build` & `docker push`** | `BuildAndPushDockerRegistry` Step | **Kaniko Daemonless Engine** | Builds and pushes Docker images safely inside Kubernetes without Docker socket exposure. |
| **Manual Verification** | `Automated Pipeline Status & Notifications` | Harness Pipeline Dashboard / Slack Webhook | Instant feedback on build pass/fail with full step logs. |

---

### ⚙️ DEVOPS TASK 1: Setup Infrastructure Connectors & Secrets
> **Role Tag**: ⚙️ **DEVOPS TASK**

Before running automated pipelines, the DevOps Engineer sets up secure connections to Git and Container Registries in the Harness Platform:

1. **GitHub Connector**:
   - **Tool**: Harness Connector + GitHub Personal Access Token (PAT).
   - **Secret**: `github_pat` stored in Harness Vault.
   - **Purpose**: Grants Harness read/write access to clone repositories and set up webhooks.

2. **DockerHub Registry Connector**:
   - **Tool**: Harness Docker Registry Connector.
   - **Secret**: `dockerhub_password` stored in Harness Vault.
   - **Purpose**: Authenticates image pushes securely without hardcoding passwords in scripts.

---

### ⚙️ DEVOPS TASK 2: Define Pipeline-as-Code Specification (`harness-ci-pipeline.yaml`)
> **Role Tag**: ⚙️ **DEVOPS TASK**

The DevOps Engineer creates the declarative YAML specification that orchestrates the entire CI workflow:

```yaml
pipeline:
  name: Core_Banking_Auth_CI_Pipeline
  identifier: Core_Banking_Auth_CI_Pipeline
  projectIdentifier: default
  orgIdentifier: default
  tags:
    domain: banking
    service: auth-service
  codebase:
    connectorRef: github_connector
    repoName: Harness-Projects
    build: <+input>
  stages:
    - stage:
        name: Build_Test_and_Push_Image
        identifier: Build_Test_and_Push_Image
        type: CI
        spec:
          cloneCodebase: true
          infrastructure:
            type: VM
            spec:
              type: Docker
              os: Linux
          execution:
            steps:
              # AUTOMATED STEP 1: Run Unit Tests & Build Maven Package
              - step:
                  type: Run
                  name: Maven_Build_and_Test
                  identifier: Maven_Build_and_Test
                  spec:
                    connectorRef: dockerhub_connector
                    image: maven:3.9.6-eclipse-temurin-17
                    shell: Sh
                    command: |-
                      echo "Starting Automated Maven Build & Test Suite..."
                      cd projects/01-core-banking-ci
                      mvn clean test package
                    reports:
                      type: JUnit
                      spec:
                        paths:
                          - "projects/01-core-banking-ci/target/surefire-reports/*.xml"

              # AUTOMATED STEP 2: Build Docker Image & Push to Registry (Kaniko Engine)
              - step:
                  type: BuildAndPushDockerRegistry
                  name: Build_and_Push_to_DockerHub
                  identifier: Build_and_Push_to_DockerHub
                  spec:
                    connectorRef: dockerhub_connector
                    repo: nareshbanking/auth-service
                    tags:
                      - "<+pipeline.sequenceId>"
                      - "latest"
                    dockerfile: projects/01-core-banking-ci/Dockerfile
                    context: projects/01-core-banking-ci
```

---

### ⚙️ DEVOPS TASK 3: Configure Automated Git Webhook Triggers
> **Role Tag**: ⚙️ **DEVOPS TASK**

1. In Harness UI $\rightarrow$ **Pipelines** $\rightarrow$ **Triggers** $\rightarrow$ **New Trigger**.
2. Event Source: **GitHub**.
3. Event: **Push** to `refs/heads/main`.
4. Result: Whenever a developer executes `git push origin main`, GitHub sends an automated HTTP Webhook event to Harness, instantly triggering the CI pipeline execution!

---

### ⚙️ DEVOPS TASK 4: Deep-Dive — Why DevOps Uses Kaniko Engine for Automated Builds
> **Role Tag**: ⚙️ **DEVOPS TASK (ARCHITECTURE)**

In manual testing, you ran `docker build` which communicates with your local Docker daemon (`/var/run/docker.sock`).

In automated CI environments running inside Kubernetes clusters, using a Docker daemon poses severe security risks:
- 🛑 **Docker-in-Docker (DinD) Vulnerability**: Mounting `/var/run/docker.sock` into a build container gives the container root access to the host machine node.
- 🛡️ **DevOps Solution (Kaniko Engine)**: Harness uses **Kaniko** under the hood for `BuildAndPushDockerRegistry`:
  - Executes container image builds **inside an unprivileged Kubernetes Pod**.
  - Extracts the base image, builds layers in user space, and pushes directly to DockerHub.
  - Requires **zero privileges** or host socket access, ensuring enterprise security compliance for banking apps.

---

## 🎯 Summary of Tools & Automation Value

| Phase | Developer Role | DevOps Role | Automated Tooling |
| :--- | :--- | :--- | :--- |
| **Code Creation** | Writes Java & Tests | Reviews Dockerfile & Pipeline YAML | Git, GitHub |
| **Verification** | Runs local tests (`mvn`) | Validates container build & security | Maven, JUnit, Docker |
| **Continuous Integration** | Pushes code (`git push`) | Automates pipeline, runners, triggers, secrets | Harness CI, Kaniko, DockerHub, Webhooks |
