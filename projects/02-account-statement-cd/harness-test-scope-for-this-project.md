# 🚀 Harness CD Kubernetes Rolling Update Deployment Blueprint

> **Skill Execution Output**: Executed `harness-test-scope-for-this-project` for **Project 2: Customer Account Statement Service — Kubernetes CD Deployment**.

---

# 🧠 1. Visual Overview: Dev Scope vs. DevOps Scope

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 👨‍💻 DEVELOPER (DEV) TEAM SCOPE                                               │
│ • Write Application Code & REST API Controller (`StatementController.java`)│
│ • Write JUnit 5 & MockMvc Unit/Integration Tests (`StatementControllerTest`)│
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

# 📌 2. Standard Testing Procedure (SOP)

## Phase 1: Application Testing (Manual & Automation)

### 🛠️ 1. Required Software Installations (Local Test Prerequisites)

Install these 5 software tools on your local machine before running tests:

| Tool | Required Version | Purpose | Verification Command |
| :--- | :--- | :--- | :--- |
| **Java JDK** | JDK 17+ | Compiles & runs Java Spring Boot code | `java -version` |
| **Apache Maven** | 3.9.0+ | Builds application & runs JUnit unit tests | `mvn -v` |
| **Docker Desktop** | 4.25+ | Containerizes application locally | `docker --version` |
| **Kubectl CLI** | 1.28+ | Tests & validates Kubernetes manifests | `kubectl version --client` |
| **Kubernetes Cluster** | Minikube / Kind / Docker K8s | Target K8s deployment environment | `kubectl get nodes` |

---

### 👨‍💻 Developer (Dev) Team Tasks & Test Scope

The Developer Team is responsible for application-level functional correctness before handing code over to DevOps.

#### Step 1: Run Automated Maven Unit Tests
Navigate to `projects/02-account-statement-cd` and run:
```powershell
mvn clean test
```
* **What it tests**: Executes `StatementControllerTest.java`.
* **Expected Output**: `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0` $\rightarrow$ **BUILD SUCCESS**.
* **What If Missed**: Broken REST endpoints or syntax errors will be pushed to Git, causing downstream CI/CD build failures.

#### Step 2: Run Application Locally for Manual Testing
```powershell
mvn spring-boot:run
```
* **What it tests**: Boots embedded Tomcat web server on port `8080`.

#### Step 3: Manual Endpoint Verification (PowerShell / cURL)

* **Test Health Endpoint**:
  ```powershell
  Invoke-RestMethod -Uri http://localhost:8080/api/v1/statements/health
  ```
  * **Expected Output**:
    ```json
    {
      "status": "UP",
      "service": "Customer-Account-Statement-Service",
      "version": "1.0.0",
      "environment": "Kubernetes-Deployment"
    }
    ```

* **Test Account Statement Endpoint**:
  ```powershell
  Invoke-RestMethod -Uri http://localhost:8080/api/v1/statements/ACCT-889922
  ```
  * **Expected Output**:
    ```json
    {
      "accountNumber": "ACCT-889922",
      "customerName": "Jane Doe",
      "accountType": "Savings",
      "currentBalance": 12854.5,
      "currency": "USD"
    }
    ```

---

### 🛠️ DevOps Team Tasks & Test Scope

The DevOps Team is responsible for container security, infrastructure manifests, and Harness CD pipeline orchestration.

#### Step 1: Local Docker Container & Non-Root Security Test
```powershell
# 1. Build Multi-Stage Docker Image
docker build -t account-statement-service:1.0 .

# 2. Run Container Locally
docker run -d -p 8080:8080 --name test-app account-statement-service:1.0

# 3. Test Containerized REST Endpoint
Invoke-RestMethod -Uri http://localhost:8080/api/v1/statements/health

# 4. Audit Non-Root User Security (Banking Compliance)
docker exec test-app id
# Expected Output: uid=1000(appuser) gid=1000(appgroup) -> Proves process runs as non-root!

# 5. Clean up local test container
docker stop test-app ; docker rm test-app
```

#### Step 2: Kubernetes Manifest Dry-Run Validation
Validate K8s manifests before committing to GitHub:
```powershell
# Validate Deployment Manifest Syntax
kubectl apply -f k8s/deployment.yaml --dry-run=client

# Validate Service Manifest Syntax
kubectl apply -f k8s/service.yaml --dry-run=client
```
* **Expected Output**: `validated (dry run)`.
* **What If Missed**: Syntax errors or typos in YAML will cause the Harness CD pipeline to crash during deployment execution.

---

## Phase 2: Harness CD Setup & Low-Level Configuration

Here is the step-by-step guide to configuring Harness SaaS for Kubernetes CD deployments.

---

### ⚙️ Step-by-Step Harness Configuration Guide

#### 1. Configure GitHub Connector (`github_connector`)
* **Path in Harness UI**: **Account Settings** $\rightarrow$ **Connectors** $\rightarrow$ **+ New Connector** $\rightarrow$ **GitHub**.
* **Low-Level Details**:
  * **URL**: `https://github.com/Naresh-Shekkari/Harness-Projects.git`
  * **Authentication**: Username + Personal Access Token (`github_pat` secret with `repo` and `admin:repo_hook` scopes).
* **What If Missed**: Harness cannot pull `deployment.yaml` or `service.yaml` from GitHub. CD pipeline fails with `PathNotFound` error.

#### 2. Configure DockerHub Connector (`dockerhub_connector`)
* **Path in Harness UI**: **Account Settings** $\rightarrow$ **Connectors** $\rightarrow$ **+ New Connector** $\rightarrow$ **Docker Registry**.
* **Low-Level Details**:
  * **Docker Registry URL**: `https://index.docker.io/v1/`
  * **Username**: `naresh6961`
  * **Password Secret**: `dockerhub_password` secret.
* **What If Missed**: Harness cannot verify Docker image artifacts, resulting in `403 Forbidden` registry authentication errors.

#### 3. Configure Harness Service (`account_statement_service`)
* **Path in Harness UI**: **Services** $\rightarrow$ **+ New Service**.
* **Low-Level Details**:
  * **Deployment Type**: `Kubernetes`.
  * **Manifests**: Add K8s Manifest pointing to `k8s/deployment.yaml` and `k8s/service.yaml` in GitHub branch `main`.
  * **Artifacts**: Add Docker Registry artifact pointing to `naresh6961/account-statement-service` with tag `<+input>`.

#### 4. Configure Harness Environment & Infrastructure (`production_k8s_env`)
* **Path in Harness UI**: **Environments** $\rightarrow$ **+ New Environment**.
* **Low-Level Details**:
  * **Environment Type**: `Production`.
  * **Infrastructure Definition**: Select `Kubernetes` cluster connector (`k8s_cluster_infra`) and namespace `default`.

#### 5. Configure Harness CD Pipeline (`Core_Banking_Account_Statement_CD`)
* **Path in Harness UI**: **Pipelines** $\rightarrow$ **+ New Pipeline**.
* **Stage Type**: `Deployment` $\rightarrow$ `Kubernetes`.
* **Execution Steps**:
  * Step 1: `K8sRollingDeploy` (Name: `Rollout_Deployment`, `skipDryRun: false`).
  * Rollback Step: `K8sRollingRollback` (Name: `Rollback_Deployment`).

---

### ⚠️ Common Mistakes & Pitfalls ("What If Missed")

| # | Setup Step / Configuration | What Happens If Missed? (Failure Mode) | How to Avoid / Fix |
|---|---|---|---|
| **1** | **Hardcoded Image Tag in `deployment.yaml`** | Using `image: naresh6961/app:v1.0` instead of `<+artifact.tag>` causes Harness CD to deploy the exact same old image every time! | **Always use `<+artifact.tag>`** in `deployment.yaml`. |
| **2** | **Omitting `readinessProbe` Delay** | If `initialDelaySeconds` is missing or set too low (e.g. 0s), K8s tests health before Java JVM boots up, marking pods as broken and killing them in a loop. | Set `initialDelaySeconds: 15` on readiness probes. |
| **3** | **Setting `maxUnavailable: 1` in Deployment** | During rollout, K8s kills 1 active pod before new pod is ready, causing 503 HTTP Service Unavailable errors for active customers. | Set `maxUnavailable: 0` for 100% zero-downtime banking compliance. |
| **4** | **Setting `skipDryRun: true`** | Harness skips K8s server-side YAML validation, allowing malformed manifests to crash the deployment mid-stream. | **Set `skipDryRun: false`** so Harness validates YAML via K8s API server before starting rollout. |
| **5** | **Missing `K8sRollingRollback` Step** | If a bad deployment occurs, Harness halts but leaves the cluster in a broken state without restoring the previous working release. | Always configure `K8sRollingRollback` in the pipeline `rollbackSteps` section. |
