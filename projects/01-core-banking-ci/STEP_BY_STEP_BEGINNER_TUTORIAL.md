# 🎓 Project 1: Complete Beginner's Step-by-Step Guide
## Core Banking User Authentication Microservice & Harness CI Pipeline

> **Welcome!** If you are brand new to Java, Docker, Git, and Harness, this guide was written specifically for you.  
> It covers **every single step**, software installation, code explanation, terminal command, and Harness UI click required to build this project from absolute scratch on your own machine.

---

## 📋 Table of Contents
1. [Prerequisites & Software Installation](#1-prerequisites--software-installation)
2. [Step 1: Setting Up the Project Directory](#step-1-setting-up-the-project-directory)
3. [Step 2: Writing the Spring Boot Application Code](#step-2-writing-the-spring-boot-application-code)
4. [Step 3: Running & Testing the App Locally](#step-3-running--testing-the-app-locally)
5. [Step 4: Containerizing with Docker](#step-4-containerizing-with-docker)
6. [Step 5: Understanding & Setting Up Harness CI](#step-5-understanding--setting-up-harness-ci)
7. [Step 6: Version Control with Git & Pushing to GitHub](#step-6-version-control-with-git--pushing-to-github)
8. [Summary Cheat Sheet](#summary-cheat-sheet)

---

## 🛠️ 1. Prerequisites & Software Installation

Before writing any code, install these 5 tools on your Windows machine:

| Tool | What It Does | Download / Installation Link | Verification Command |
| :--- | :--- | :--- | :--- |
| **Java JDK 17+** | Compiles & runs Java code | [Eclipse Temurin JDK 17](https://adoptium.net/) | `java -version` |
| **Apache Maven** | Manages Java dependencies & builds `.jar` | [Maven Download](https://maven.apache.org/download.cgi) | `mvn -v` |
| **Docker Desktop** | Package and run apps inside containers | [Docker Desktop Windows](https://www.docker.com/products/docker-desktop/) | `docker --version` |
| **Git** | Tracks code history & connects to GitHub | [Git for Windows](https://git-scm.com/download/win) | `git --version` |
| **Harness SaaS Account** | Free CI/CD Cloud Platform | [Harness Free Signup](https://app.harness.io/) | N/A (Web Browser) |

---

## 📂 Step 1: Setting Up the Project Directory

Open **PowerShell** or **Command Prompt** and create the project folder structure manually:

```powershell
# Create root project folder
mkdir C:\Users\Naresh\Documents\GitHub\AGY\projects\01-core-banking-ci
cd C:\Users\Naresh\Documents\GitHub\AGY\projects\01-core-banking-ci

# Create Java source code package directories
mkdir -p src/main/java/com/bank/auth
mkdir -p src/test/java/com/bank/auth
```

### Folder Hierarchy Explained:
```
projects/01-core-banking-ci/
├── pom.xml                                   <-- Build & dependency configuration
├── Dockerfile                                <-- Container image instructions
├── harness-ci-pipeline.yaml                  <-- Harness CI Automation pipeline
└── src/
    ├── main/java/com/bank/auth/
    │   ├── AuthApplication.java              <-- Main Spring Boot entrypoint
    │   └── AuthController.java               <-- REST API endpoints (/health, /login)
    └── test/java/com/bank/auth/
        └── AuthControllerTest.java           <-- Automated unit test suite
```

---

## 💻 Step 2: Writing the Spring Boot Application Code

### File 1: Maven Build File (`pom.xml`)
Create `pom.xml` in `projects/01-core-banking-ci/`.

**What it does:**
- Specifies Java 17 compatibility.
- Downloads Spring Boot Web starter (for building REST APIs).
- Downloads Spring Boot Test starter (JUnit 5 + Mockito for unit testing).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.2</version>
        <relativePath/>
    </parent>

    <groupId>com.bank.auth</groupId>
    <artifactId>auth-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>Core Banking User Auth Service</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Web dependency for REST Controllers -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Test framework (JUnit 5 + MockMvc) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven plugin to package application as executable jar -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### File 2: Main Application Class (`src/main/java/com/bank/auth/AuthApplication.java`)
**What it does:** The starting point of the Java application. `@SpringBootApplication` initializes the Spring IoC container and starts the embedded Tomcat web server on port 8080.

```java
package com.bank.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
```

---

### File 3: REST API Controller (`src/main/java/com/bank/auth/AuthController.java`)
**What it does:** Exposes HTTP endpoints for banking clients:
- `GET /api/v1/auth/health` — Health check endpoint used by Kubernetes and load balancers.
- `POST /api/v1/auth/login` — Authenticates user credentials (`bankuser` / `securepass123`) and returns a JWT token.

```java
package com.bank.auth;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of(
            "status", "UP",
            "service", "Core-Banking-Auth-Service",
            "version", "1.0.0"
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if ("bankuser".equals(username) && "securepass123".equals(password)) {
            return Map.of(
                "authenticated", true,
                "token", "jwt-token-banking-auth-89324729384729384",
                "message", "Login successful"
            );
        }

        return Map.of(
            "authenticated", false,
            "message", "Invalid credentials"
        );
    }
}
```

---

### File 4: Unit Test Suite (`src/test/java/com/bank/auth/AuthControllerTest.java`)
**What it does:** Tests application logic automatically without starting a real browser or manual testing:
- Checks if health endpoint returns HTTP 200 OK.
- Tests valid login returning `authenticated: true`.
- Tests invalid login returning `authenticated: false`.

```java
package com.bank.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Core-Banking-Auth-Service"));
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        String payload = "{\"username\": \"bankuser\", \"password\": \"securepass123\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    public void testFailedLogin() throws Exception {
        String payload = "{\"username\": \"wronguser\", \"password\": \"wrongpass\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
```

---

## 🧪 Step 3: Running & Testing the App Locally

### 3.1 Run Unit Tests via Maven
In your terminal, navigate to `projects/01-core-banking-ci/` and run:

```powershell
mvn clean test
```

**Expected Output:**
```text
[INFO] Running com.bank.auth.AuthControllerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 3.2 Run the Web Application Locally
To start the application on your computer:

```powershell
mvn spring-boot:run
```

Open your browser and navigate to: `http://localhost:8080/api/v1/auth/health`  
You will see the JSON response:
```json
{
  "status": "UP",
  "service": "Core-Banking-Auth-Service",
  "version": "1.0.0"
}
```
*(Press `Ctrl + C` in PowerShell to stop the app).*

---

## 🐳 Step 4: Containerizing with Docker

### File: `Dockerfile`
Create `Dockerfile` in `projects/01-core-banking-ci/`.

**Why use a multi-stage Dockerfile?**
- **Stage 1 (Builder)** uses a full Maven image to compile code.
- **Stage 2 (Runtime)** copies ONLY the compiled `.jar` into a tiny JRE image (reducing image size from 600MB down to 180MB and removing build tools for security).
- Uses a **non-root user** (`appuser`) for enterprise banking compliance.

```dockerfile
# STAGE 1: Build & Package Java Application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build jar
COPY src ./src
RUN mvn package -DskipTests -B

# STAGE 2: Lightweight Runtime Environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy jar from builder stage
COPY --from=builder /app/target/auth-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 4.1 Build Docker Image Locally
Ensure **Docker Desktop** is running, then run:

```powershell
docker build -t auth-service:1.0 .
```

### 4.2 Run Docker Container Locally
```powershell
docker run -d -p 8080:8080 --name my-auth-app auth-service:1.0
```

Verify in browser at `http://localhost:8080/api/v1/auth/health`.

Stop and clean up container:
```powershell
docker stop my-auth-app
docker rm my-auth-app
```

---

## ⚡ Step 5: Understanding & Setting Up Harness CI

### Why do we need Harness if we can run Maven and Docker manually?
In a real company:
- You cannot ask developers to build and test code manually on their laptops.
- You need a central, automated server that tests **every single git push**, checks security, builds the container image, and pushes it to DockerHub automatically.
- Harness manages logs, metrics, secrets, and security policies centrally.

---

### Harness Architecture Components

```
[ GitHub Repo ] ────► [ Harness SaaS UI ] ────► [ Harness Delegate (Worker) ]
                            │                               │
                      Pipeline Config              Runs Maven & Kaniko
```

1. **Harness SaaS UI**: The web browser portal where you view pipeline status and logs.
2. **Harness Delegate**: A lightweight worker process running in your cluster or VM that actually executes the build commands locally within your network.

---

### Step-by-Step Harness UI Setup (Click-by-Click)

#### 1. Create a Harness Secret for DockerHub & GitHub
1. Log into [app.harness.io](https://app.harness.io/).
2. Select your Project (or create **Retail_Banking**).
3. Go to **Account Settings** $\rightarrow$ **Secrets** $\rightarrow$ **+ New Secret** $\rightarrow$ **Text**.
4. Name: `dockerhub_password` | Value: Your DockerHub Access Token / Password.
5. Create another Secret: `github_pat` | Value: Your GitHub Personal Access Token.

#### 2. Create Connectors in Harness
- **GitHub Connector**: Go to **Connectors** $\rightarrow$ **+ New Connector** $\rightarrow$ **GitHub**.
  - Name: `github_banking_repo_connector`
  - URL: `https://github.com/Naresh-Shekkari/Harness-Projects.git`
  - Auth: Username + `github_pat` secret.
- **DockerHub Connector**: Go to **Connectors** $\rightarrow$ **+ New Connector** $\rightarrow$ **Docker Registry**.
  - Name: `dockerhub_connector`
  - Docker Registry URL: `https://index.docker.io/v1/`
  - Username: Your DockerHub Username
  - Password Secret: `dockerhub_password`

#### 3. Install Harness Delegate
1. Go to **Delegates** $\rightarrow$ **Install Delegate** $\rightarrow$ **Docker** or **Kubernetes**.
2. Run the provided command in your terminal (e.g., `docker run ... harness/delegate:latest`).
3. Verify status shows **Connected (Heartbeat OK)**.

---

### The Harness Pipeline Specification (`harness-ci-pipeline.yaml`)

This YAML file tells Harness exactly how to automate the build:

```yaml
pipeline:
  name: Core_Banking_Auth_CI_Pipeline
  identifier: Core_Banking_Auth_CI_Pipeline
  projectIdentifier: Retail_Banking
  orgIdentifier: Enterprise_Banking_Org
  stages:
    - stage:
        name: Build_Test_and_Push_Image
        identifier: Build_Test_and_Push_Image
        type: CI
        spec:
          cloneCodebase: true
          infrastructure:
            type: KubernetesDirect
            spec:
              connectorRef: harness_k8s_delegate_connector
              namespace: harness-ci-builds
          execution:
            steps:
              # Step 1: Run Unit Tests & Build Jar
              - step:
                  type: Run
                  name: Maven_Build_and_Test
                  spec:
                    connectorRef: dockerhub_connector
                    image: maven:3.9.6-eclipse-temurin-17
                    command: |-
                      cd projects/01-core-banking-ci
                      mvn clean test package
                    reports:
                      type: JUnit
                      spec:
                        paths:
                          - "projects/01-core-banking-ci/target/surefire-reports/*.xml"

              # Step 2: Build & Push Docker Image (Daemonless Kaniko Engine)
              - step:
                  type: BuildAndPushDockerRegistry
                  name: Build_and_Push_to_DockerHub
                  spec:
                    connectorRef: dockerhub_connector
                    repo: nareshbanking/auth-service
                    tags:
                      - "<+pipeline.sequenceId>"
                      - "latest"
                    dockerfile: projects/01-core-banking-ci/Dockerfile
                    context: projects/01-core-banking-ci

  triggers:
    - trigger:
        name: GitHub_Commit_Trigger
        enabled: true
        source:
          type: Webhook
          spec:
            type: Github
            event: Push
            connectorRef: github_banking_repo_connector
            repoName: Harness-Projects
            branches:
              - main
```

---

## 🐙 Step 6: Version Control with Git & Pushing to GitHub

Here are the exact terminal commands used to push this project to GitHub:

### 1. Initialize Git Repository
```powershell
cd C:\Users\Naresh\Documents\GitHub\AGY
git init
```

### 2. Create `.gitignore` File
Create `.gitignore` to prevent temporary build files (`target/`, `.class`, `.jar`) from being uploaded to Git:

```text
target/
*.class
*.jar
*.war
.idea/
*.iml
```

### 3. Add & Commit Files Locally
```powershell
git add .
git commit -m "feat: add Project 01 Core Banking Microservice CI Pipeline"
```

### 4. Link Local Git to Remote GitHub Repository & Push
```powershell
# Set origin remote URL
git remote add origin https://github.com/Naresh-Shekkari/Harness-Projects.git

# Set main branch name
git branch -M main

# Pull remote README if present and rebase
git pull origin main --rebase

# Push code to GitHub
git push -u origin main
```

---

## 📌 Summary Cheat Sheet

| Task | Command |
| :--- | :--- |
| **Run Unit Tests** | `mvn clean test` |
| **Run Application Locally** | `mvn spring-boot:run` |
| **Build Docker Image** | `docker build -t auth-service:1.0 .` |
| **Run Docker Container** | `docker run -p 8080:8080 auth-service:1.0` |
| **Check Git Status** | `git status` |
| **Push Code Changes** | `git add . ; git commit -m "msg" ; git push` |
| **Test Endpoint in PowerShell** | `Invoke-RestMethod -Uri http://localhost:8080/api/v1/auth/health` |

---

> **Congratulations!** You now understand every component of this project from local code compilation to containerization and cloud-native CI/CD automation with Harness!

To trigger the build pushed the changes to github.
