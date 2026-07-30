---
name: harness-k8s-cd-deployment
description: >
  Guide for configuring, deploying, and orchestrating Kubernetes Continuous Deployment (CD)
  stages in Harness SaaS. Use this skill whenever deploying containerized microservices to
  Kubernetes clusters, writing K8s manifests (Deployment & Service), configuring Harness CD
  Services, Environments, Infrastructure Definitions, or troubleshooting Rolling Updates and automated Rollbacks.
---

# 🚀 Harness CD Kubernetes Rolling Update Deployment Blueprint

> **Skill Purpose**: Encapsulates the standard operating procedure (SOP), configuration patterns, K8s manifests, Harness CD pipeline specifications, and troubleshooting guardrails for automated Kubernetes zero-downtime rolling update deployments.

---

## 📌 1. Standard Operating Procedure (SOP)

### Phase 1: Application & Manifest Readiness
1. **Container Image**: Verify image exists in registry (`naresh6961/account-statement-service:<tag>`).
2. **K8s Manifest Specs**:
   - `deployment.yaml`: Rolling Update strategy (`maxSurge: 1`, `maxUnavailable: 0`), liveness probe (`/health`), readiness probe, memory/CPU requests & limits.
   - `service.yaml`: NodePort or ClusterIP service mapping port `8080` to container port `8080`.

### Phase 2: Harness CD Setup
1. **Harness Service**: Define `account_statement_service` pointing to DockerHub artifact and K8s manifests in GitHub.
2. **Harness Environment**: Define `production_k8s_env` linked to Kubernetes cluster infrastructure (`k8s_cluster_infra`).
3. **Execution Stage**: Configure `K8sRollingDeploy` step and automated `K8sRollingRollback` step.

---

## 🛠️ 2. Production Blueprint Artifacts

- **Deployment Manifest**: [`k8s/deployment.yaml`](file:///C:/Users/Naresh/Documents/GitHub/AGY/projects/02-account-statement-cd/k8s/deployment.yaml)
- **Service Manifest**: [`k8s/service.yaml`](file:///C:/Users/Naresh/Documents/GitHub/AGY/projects/02-account-statement-cd/k8s/service.yaml)
- **CD Pipeline Specification**: [`harness-cd-pipeline.yaml`](file:///C:/Users/Naresh/Documents/GitHub/AGY/projects/02-account-statement-cd/harness-cd-pipeline.yaml)

---

## 🛡️ 3. Troubleshooting & Mandatory Guardrails

1. **Artifact Expression Tag**: Always use `<+artifact.tag>` in `deployment.yaml` so Harness CD dynamically injects the target image tag during rollout.
2. **Probe Initial Delays**: Ensure `initialDelaySeconds` in liveness/readiness probes allows Spring Boot JVM to finish initialization (15-20 seconds).
3. **Zero-Downtime Settings**: `maxUnavailable: 0` ensures existing pods stay online until new pods pass readiness probes.
