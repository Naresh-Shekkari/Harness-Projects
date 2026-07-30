---
name: harness-test-scope-for-this-project
description: >
  Guide for complete project Test Scope. Manual Test Scope, Automation scope steps and within that which steps are related to Dev team steps and Which steps are related DevOps team - differentiate by adding the heading. So everyone can easily understand the scope of the test scenarios.
  If any configurations are there explain with low level info, how to configure the setup step by step clearly point to point. If I missed what will happen that also explain.
  commonly making mistakes also point out.
---

# 🚀 Harness CD Kubernetes Rolling Update Deployment Blueprint

> **Skill Purpose**: Encapsulates the standard Testing procedure (SOP), manual testing scope in local, automation test steps, differentiates dev scope vs devops scope, and details low-level configuration steps with failure modes ("What If Missed") and common mistakes.

---

## 🤖 Mandatory Agent Execution Instructions

When this skill is activated (by prompt or user request for test scope), the AI assistant **MUST** generate a complete, low-level, highly detailed test scope document following these exact sections:

---

## 🧠 1. Visual Overview: Dev Scope vs. DevOps Scope

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 👨‍💻 DEVELOPER (DEV) TEAM SCOPE                                               │
│ • Write Application Code & REST API Controller                             │
│ • Write JUnit 5 & MockMvc Unit/Integration Tests                           │
│ • Run Local Maven Build (`mvn clean test package`)                          │
│ • Verify Local REST API (`http://localhost:8080/api/v1/statements/...`)    │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼ (Code Pushed to GitHub)
┌─────────────────────────────────────────────────────────────────────────────┐
│ 🛠️ DEVOPS TEAM SCOPE                                                        │
│ • Write Multi-Stage Dockerfile (`eclipse-temurin:17-jre-alpine`)            │
│ • Write Kubernetes Manifests (`deployment.yaml` & `service.yaml`)          │
│ • Validate Manifests (`kubectl apply --dry-run=client`)                     │
│ • Configure Harness SaaS Connectors (GitHub, DockerHub, K8s Cluster)        │
│ • Configure Harness CD Pipeline (`K8sRollingDeploy` & `K8sRollingRollback`) │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 📌 2. Standard Testing Procedure (SOP)

### Phase 1: Application Testing (Manual & Automation)

#### 🛠️ 1. Required Software Installations (Local Test Prerequisites)
List required software dependencies, version requirements, purpose, and verification commands:
- **Java JDK 17+** (`java -version`)
- **Apache Maven 3.9.0+** (`mvn -v`)
- **Docker Desktop 4.25+** (`docker --version`)
- **Kubectl CLI 1.28+** (`kubectl version --client`)
- **Kubernetes Cluster** (`kubectl get nodes`)

---

#### 👨‍💻 Developer (Dev) Team Tasks & Test Scope
Under explicit heading `### 👨‍💻 Developer (Dev) Team Tasks & Test Scope`, detail:
1. **Automated Unit Testing**: `mvn clean test` (Runs MockMvc tests, verifies HTTP 200 OK & JSON response fields). Explain *What If Missed*.
2. **Local App Execution**: `mvn spring-boot:run` (Starts Tomcat on port 8080).
3. **Manual REST Endpoint Verification**: `Invoke-RestMethod` / `curl` commands testing `/health` and API endpoints.

---

#### 🛠️ DevOps Team Tasks & Test Scope
Under explicit heading `### 🛠️ DevOps Team Tasks & Test Scope`, detail:
1. **Local Docker Build & Non-Root Security Test**:
   - `docker build -t app:1.0 .`
   - `docker run -d -p 8080:8080 --name test-app app:1.0`
   - `docker exec test-app id` (Audit non-root user `appuser` / UID 1000).
2. **Kubernetes Manifest Dry-Run Validation**:
   - `kubectl apply -f k8s/deployment.yaml --dry-run=client`
   - `kubectl apply -f k8s/service.yaml --dry-run=client`

---

### Phase 2: Harness CD Setup & Low-Level Configuration

#### ⚙️ Step-by-Step Harness Configuration Guide
Explain point-by-point low-level instructions and *What If Missed* for each:
1. **GitHub Connector (`github_connector`)**: Connects to GitHub with PAT credentials (`repo` and `admin:repo_hook` scopes). *What If Missed*: Pipeline fails with `PathNotFound`.
2. **DockerHub Connector (`dockerhub_connector`)**: Connects to Docker Registry. *What If Missed*: Pipeline fails with `403 Forbidden`.
3. **Harness Service (`account_statement_service`)**: Configures Kubernetes manifest paths and Docker image artifact `<+input>`.
4. **Harness Environment (`production_k8s_env`)**: Configures production K8s cluster infrastructure definition.
5. **Harness CD Pipeline (`Core_Banking_Account_Statement_CD`)**: Configures `K8sRollingDeploy` (`skipDryRun: false`) and `K8sRollingRollback` steps.

---

### ⚠️ Common Mistakes & Pitfalls ("What If Missed")

Include a comprehensive matrix table detailing:
- Hardcoded image tags vs `<+artifact.tag>`
- Omitting `readinessProbe` delay (`initialDelaySeconds: 15`)
- Setting `maxUnavailable: 1` vs `maxUnavailable: 0`
- Setting `skipDryRun: true`
- Missing `K8sRollingRollback` step
- **Docker Image Metadata Load Cancellation (`ERROR [internal] load metadata for docker.io/...`)**: Caused by unauthenticated Docker daemon session rate limits or port 443 network blocks. Fix: Run `Test-NetConnection -ComputerName registry-1.docker.io -Port 443` (Step 1) and `docker login` (Step 2).

