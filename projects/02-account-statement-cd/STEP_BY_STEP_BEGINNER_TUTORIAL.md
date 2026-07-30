# 🎓 Project 2: Complete Beginner's Step-by-Step Guide
## Customer Account Statement Microservice & Harness CD Kubernetes Deployment

> **Welcome to Project 2!** In Project 1, we built an automated **CI Pipeline** to compile Java code and push Docker container images.  
> In **Project 2**, we learn **Continuous Deployment (CD)**: deploying container images onto a **Kubernetes (K8s)** cluster with Zero-Downtime Rolling Updates!

---

## 📋 Table of Contents
1. [Prerequisites & Concepts](#1-prerequisites--concepts)
2. [Step 1: Project Folder & Spring Boot Code](#step-1-project-folder--spring-boot-code)
3. [Step 2: Containerizing with Docker](#step-2-containerizing-with-docker)
4. [Step 3: Understanding & Writing Kubernetes Manifests](#step-3-understanding--writing-kubernetes-manifests)
5. [Step 4: Harness CD Pipeline Setup](#step-4-harness-cd-pipeline-setup)
6. [Step 5: Common Mistakes & Troubleshooting Guide](#step-5-common-mistakes--troubleshooting-guide)
7. [Summary Cheat Sheet](#summary-cheat-sheet)

---

## 🧠 1. Prerequisites & Concepts

### What is Kubernetes Continuous Deployment (CD)?

```
┌─────────────────────────────────────────────────────────────┐
│ 🐳 DockerHub Image Registry                                 │
│ Image: naresh6961/account-statement-service:v1.0           │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼ (Harness CD Triggers Rollout)
┌─────────────────────────────────────────────────────────────┐
│ ☸️ Kubernetes Cluster (Rolling Update Strategy)              │
│ Pod 1 (v1.0) ──► Pod 2 (v1.0) ──► Seamless Zero Downtime    │
└─────────────────────────────────────────────────────────────┘
```

---

## 📄 Step 3: Line-by-Line Kubernetes Manifest Breakdown

### File 1: Deployment Manifest (`k8s/deployment.yaml`)

```yaml
1: apiVersion: apps/v1
2: kind: Deployment
3: metadata:
4:   name: account-statement-service
5:   namespace: default
6:   labels:
7:     app: account-statement-service
8:     tier: backend
9: spec:
10:   replicas: 2
11:   strategy:
12:     type: RollingUpdate
13:     rollingUpdate:
14:       maxSurge: 1
15:       maxUnavailable: 0
16:   selector:
17:     matchLabels:
18:       app: account-statement-service
19:   template:
20:     metadata:
21:       labels:
22:         app: account-statement-service
23:     spec:
24:       containers:
25:         - name: account-statement-service
26:           image: naresh6961/account-statement-service:<+artifact.tag>
27:           imagePullPolicy: Always
28:           ports:
29:             - containerPort: 8080
30:               name: http
31:           resources:
32:             requests:
33:               memory: "256Mi"
34:               cpu: "250m"
35:             limits:
36:               memory: "512Mi"
37:               cpu: "500m"
38:           livenessProbe:
39:             httpGet:
40:               path: /api/v1/statements/health
41:               port: 8080
42:             initialDelaySeconds: 20
43:             periodSeconds: 10
44:           readinessProbe:
45:             httpGet:
46:               path: /api/v1/statements/health
47:               port: 8080
48:             initialDelaySeconds: 15
49:             periodSeconds: 5
```

#### 🔍 Low-Level Line-by-Line Breakdown:

* **Line 1-2 (`apiVersion: apps/v1`, `kind: Deployment`)**: Declares a Kubernetes Workload resource that manages a set of identical Pods.
* **Line 10 (`replicas: 2`)**: Instructs Kubernetes Control Plane to keep **2 active instances (Pods)** running at all times for high availability.
* **Lines 11-15 (`RollingUpdate`, `maxSurge: 1`, `maxUnavailable: 0`)**:
  * `maxSurge: 1`: Allows K8s to spin up 1 extra Pod during rollout (3 Pods temporarily).
  * `maxUnavailable: 0`: **WITH `maxUnavailable: 0`**: Ensures zero existing Pods are terminated until new Pods pass readiness checks! Guaranteeing zero-downtime!
  * **WITHOUT `maxUnavailable: 0`**: K8s may terminate active Pods before new Pods are ready, causing temporary HTTP 503 outage errors to customers.
* **Line 26 (`image: naresh6961/account-statement-service:<+artifact.tag>`)**:
  * `<+artifact.tag>` is a **Harness Dynamic Expression**. During CD execution, Harness replaces `<+artifact.tag>` with the exact DockerHub tag selected for deployment (e.g. `v1.0.1`).
* **Lines 31-37 (`resources: requests & limits`)**:
  * `requests`: Guarantees memory (256MB) and CPU (0.25 core) allocation on K8s worker node.
  * `limits`: Prevents memory leaks by killing containers exceeding 512MB memory.
* **Lines 38-49 (`livenessProbe` & `readinessProbe`)**:
  * `readinessProbe`: K8s checks `/api/v1/statements/health` every 5s. K8s does NOT route live banking traffic to a pod until this probe returns HTTP 200 OK.
  * `livenessProbe`: K8s checks `/api/v1/statements/health` every 10s. If the Java process freezes or deadlocks, K8s automatically restarts the pod!

---

### File 2: Service Manifest (`k8s/service.yaml`)

```yaml
1: apiVersion: v1
2: kind: Service
3: metadata:
4:   name: account-statement-service
5:   namespace: default
6: spec:
7:   type: NodePort
8:   selector:
9:     app: account-statement-service
10:  ports:
11:    - port: 8080
12:      targetPort: 8080
13:      nodePort: 30080
```

#### 🔍 Low-Level Line-by-Line Breakdown:

* **Line 7 (`type: NodePort`)**: Exposes the Kubernetes service externally on a static port across all cluster nodes.
* **Line 8-9 (`selector: app: account-statement-service`)**: Routes network traffic to all Pods labeled `app: account-statement-service`.
* **Line 11-13 (`port: 8080`, `targetPort: 8080`, `nodePort: 30080`)**:
  * `nodePort: 30080`: External port accessed by developers (e.g. `http://localhost:30080`).
  * `port: 8080`: Cluster internal service port.
  * `targetPort: 8080`: Container port listening inside the Java Spring Boot pod.

---

## ⚡ Step 4: Line-by-Line Harness CD Pipeline Breakdown (`harness-cd-pipeline.yaml`)

```yaml
1: pipeline:
2:   name: Core_Banking_Account_Statement_CD
3:   identifier: Core_Banking_Account_Statement_CD
4:   projectIdentifier: default_project
5:   orgIdentifier: default
6:   stages:
7:     - stage:
8:         name: Deploy_Account_Statement_Service
9:         identifier: Deploy_Account_Statement_Service
10:        type: Deployment
11:        spec:
12:          deploymentType: Kubernetes
13:          execution:
14:            steps:
15:              - step:
16:                  type: K8sRollingDeploy
17:                  name: Rollout_Deployment
18:                  identifier: Rollout_Deployment
19:                  spec:
20:                    skipDryRun: false
```

#### 🔍 Low-Level Line-by-Line Breakdown:

* **Line 10 (`type: Deployment`)**: Tells Harness this is a Continuous Deployment stage.
* **Line 12 (`deploymentType: Kubernetes`)**: Targets a Kubernetes cluster infrastructure.
* **Line 16 (`type: K8sRollingDeploy`)**: Executes automated Kubernetes rolling update, monitoring readiness probes and tracking rollout progress.
* **Rollback Safeguard**: If any new pod crashes or fails readiness probes, Harness automatically triggers `K8sRollingRollback` to safely restore the previous working release!

---

## ⚠️ Step 5: Common Mistakes & Troubleshooting Guide

| # | Common Mistake / Error | Consequence / Symptom | Fix / Solution |
|---|---|---|---|
| **1** | Hardcoding container image tag in `deployment.yaml` | Harness CD cannot deploy new image versions automatically. | Always use `<+artifact.tag>` expression in `deployment.yaml`. |
| **2** | Omitting `readinessProbe` initial delay | K8s checks health before Spring Boot finishes booting, marking pods as failed. | Set `initialDelaySeconds: 15` on readiness probes. |
| **3** | `maxUnavailable` left at default | K8s takes down active pods during rollout, causing brief downtime. | Explicitly set `maxUnavailable: 0` for banking zero-downtime. |
| **4** | Docker Metadata Load Cancellation (`ERROR [internal] load metadata for docker.io/...`) | `docker build` fails/cancels while loading image manifests. | Perform Step 1 (Connectivity Test) & Step 2 (`docker login`) below. |

---

### 🚨 Detailed Troubleshooting: Docker Image Metadata Load Cancellation

* **Error Output**:
  ```text
  ERROR [internal] load metadata for docker.io/library/maven:3.9.6-eclipse-temurin-17
  CANCELED [internal] load metadata for docker.io/library/eclipse-temurin:17-jre-alpine
  ```
* **Root Cause**: Docker Daemon failed to complete the HTTPS pre-flight manifest handshake (Port 443) with DockerHub (`registry-1.docker.io`) due to unauthenticated session rate limits, active VPN/proxy barriers, or DNS resolution timeouts inside the Docker Engine daemon.

---

#### 🔧 Step-by-Step Resolution Guide:

### Step 1: Verify Internet & DockerHub Connectivity
Run this command in PowerShell to check if your computer can reach DockerHub on port 443:
```powershell
Test-NetConnection -ComputerName registry-1.docker.io -Port 443
```
* **Expected Output**: `TcpTestSucceeded : True`.
* **If False**: Your internet connection, VPN, or proxy is blocking DockerHub. Disconnect any active VPN and retry.

---

### Step 2: Authenticate Docker CLI to DockerHub
Logging into DockerHub grants higher API rate limits and bypasses anonymous throttling:
```powershell
docker login
```
* **Action**: Enter your DockerHub username (`naresh6961`) and DockerHub Personal Access Token / password.
* **Result**: Authenticates your local Docker daemon session, preventing metadata pull cancellations and rate limiting.

