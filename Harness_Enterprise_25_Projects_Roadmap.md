# Enterprise Harness CI/CD Master Roadmap: 25 Production Banking Projects

Welcome to your Enterprise Harness DevOps Apprenticeship! Below is your comprehensive, step-by-step 25-Project Master Roadmap. 

Every project in this roadmap is modeled after real-world production systems implemented across Fortune 500 banks and financial institutions (Core Banking, Payment Gateways, Fraud Detection, ATM Switches, Stock Trading Platforms, and Credit Risk Engines).

---

## 🗺️ Master Curriculum Overview

```
 [Level 1: Beginner] ──► [Level 2: Intermediate] ──► [Level 3: Advanced] ──► [Level 4: Enterprise Expert]
   Projects 1 - 6             Projects 7 - 13             Projects 14 - 19             Projects 20 - 25
```

---

## 🟢 LEVEL 1: BEGINNER (Foundations & Core Harness Mechanics)

### Project 1: Core Banking Microservice - Basic CI Pipeline
* **Target Domain**: Retail Banking - User Authentication Service (Java Spring Boot).
* **Tech Stack**: GitHub, Harness CI, Maven, Docker, DockerHub.
* **Objective**: Build your first automated Harness CI pipeline that triggers on git commit, compiles Java code, runs unit tests, builds an immutable Docker image, and pushes it to DockerHub.

### Project 2: Account Statement Service - Standard Kubernetes Deployment (CD)
* **Target Domain**: Retail Banking - Customer Account Statement API.
* **Tech Stack**: Harness CD, Docker, Kubernetes (Minikube / Kind), Kube-Manifests.
* **Objective**: Configure your first Harness CD stage to pull a container image and perform a Rolling Update deployment onto a Kubernetes cluster with automatic rollout tracking.

### Project 3: Credit Card Authorization Service - End-to-End CI/CD Integration
* **Target Domain**: Credit Card Processing - Authorization Gateway.
* **Tech Stack**: GitHub, Harness CI/CD, Maven, Docker, Kubernetes, Slack Notifications.
* **Objective**: Chain a CI stage and a CD stage into a single continuous pipeline with automated triggers and Slack channel notifications for deployment events.

### Project 4: ATM Cash Dispenser API - Environment Progression (Dev → Staging → Prod)
* **Target Domain**: Self-Service Banking - ATM Terminal Controller.
* **Tech Stack**: Harness CD, Multi-Environment Infrastructure Definitions, Kubernetes Namespaces.
* **Objective**: Build a pipeline that promotes an application through Dev, Staging, and Production environments with manual approval gates between stages.

### Project 5: Loan Eligibility Engine - ConfigMaps & Secrets Injection
* **Target Domain**: Consumer Lending - Automated Credit Scoring API.
* **Tech Stack**: Harness Secrets Manager, HashiCorp Vault, Kubernetes Secrets, ConfigMaps.
* **Objective**: Securely inject dynamic database connection strings and secret API keys into a deployed Kubernetes pod without exposing sensitive data in Git or Harness UI.

### Project 6: Wealth Management Portal - Multi-Service Parallel Pipelines
* **Target Domain**: Private Banking - Portfolio & Stock Ticker Services.
* **Tech Stack**: Harness CI/CD, Docker, Kubernetes, Parallel Execution Steps.
* **Objective**: Orchestrate parallel builds and deployments for multiple microservices within a single pipeline using execution dependencies.

---

## 🟡 LEVEL 2: INTERMEDIATE (Security, AWS Infrastructure, & Enterprise Tooling)

### Project 7: Payment Gateway API - Security & Code Quality Gateways
* **Target Domain**: Merchant Payment Processing System.
* **Tech Stack**: Harness CI, SonarQube, Trivy Vulnerability Scanner, OWASP Dependency Check.
* **Objective**: Integrate code quality (SonarQube) and container vulnerability scanning (Trivy) into CI. Fail the pipeline automatically if critical CVE vulnerabilities or low code coverage is detected.

### Project 8: Commercial Loan Portal - AWS EKS & Amazon ECR Integration
* **Target Domain**: Commercial Banking - Enterprise Loan Application Portal.
* **Tech Stack**: Harness CD, AWS ECR, AWS EKS, AWS IAM Roles for Service Accounts (IRSA).
* **Objective**: Configure Harness cloud connectors with AWS using IAM roles, build images to Amazon ECR, and deploy securely onto Amazon EKS.

### Project 9: SWIFT International Transfers - Helm Chart Deployment Pipeline
* **Target Domain**: Wire Transfers - SWIFT Messaging Adapter Service.
* **Tech Stack**: Harness CD, Helm v3, Helm Repositories (Nexus/JFrog), AWS EKS.
* **Objective**: Package a microservice using Helm charts, manage dynamic `values.yaml` files across environments, and execute automated Helm upgrades and rollbacks via Harness.

### Project 10: Fraud Detection Engine - Enterprise Artifact Management (Nexus & JFrog)
* **Target Domain**: Financial Crime - Real-Time AI Fraud Prevention Engine.
* **Tech Stack**: Harness CI/CD, Sonatype Nexus / JFrog Artifactory, Maven, Docker.
* **Objective**: Publish compiled JAR artifacts and container images to an enterprise private artifact repository (Nexus) with immutable semantic versioning.

### Project 11: Currency Exchange Rate API - Custom Delegate Setup & Network Isolation
* **Target Domain**: Foreign Exchange (FX) - Real-Time Rates Engine.
* **Tech Stack**: Harness Delegate, Docker / Kubernetes Delegate, Private Subnet VPC.
* **Objective**: Install, configure, monitor, and troubleshoot a production-grade Harness Delegate inside a completely isolated private banking subnet with zero inbound internet ports.

### Project 12: Online Banking Mobile API - Triggers & Automated GitOps Event Handlers
* **Target Domain**: Mobile Banking - REST API Gateway.
* **Tech Stack**: GitHub Webhooks, Harness Webhook Triggers, Git Experience (Git-Sync).
* **Objective**: Enable full Git-Sync for Harness pipeline YAMLs. Automate pipeline execution via GitHub webhooks on pull request merges and release tags.

### Project 13: Core Savings Database Migrations - Automated SQL Schema Deployments
* **Target Domain**: Core Banking - Savings Account Database Layer.
* **Tech Stack**: Harness CD, Flyway / Liquibase, PostgreSQL / Oracle DB, Kubernetes Jobs.
* **Objective**: Run safe, automated database schema migrations (Liquibase/Flyway) as a pre-deployment step before deploying the new backend service code.

---

## 🟠 LEVEL 3: ADVANCED (Production Rollouts, AI Verification & Infrastructure as Code)

### Project 14: High-Frequency Stock Trading Platform - Blue-Green Zero-Downtime Deployment
* **Target Domain**: Capital Markets - Equities Trading Engine.
* **Tech Stack**: Harness CD, Kubernetes Services, NGINX Ingress, Blue-Green Strategy.
* **Objective**: Implement a true zero-downtime Blue-Green deployment strategy. Route live trading traffic instantly between active and preview environments with zero dropped socket connections.

### Project 15: Cross-Border Wire Transfers - Canary Deployment with Automated Verification
* **Target Domain**: Global Transfers - Cross-Border Settlement Engine.
* **Tech Stack**: Harness CD, Canary Strategy, Prometheus, Grafana, Datadog.
* **Objective**: Deploy a 10% Canary release, route real production traffic, analyze log error rates via Datadog, and automatically roll back within 30 seconds if HTTP 500 errors spike.

### Project 16: Mortgages & Escrow Platform - Infrastructure Provisioning via Terraform & Harness
* **Target Domain**: Consumer Banking - Mortgage Processing Engine.
* **Tech Stack**: Harness CD, Terraform Cloud/CLI, AWS S3 State Backend, EKS.
* **Objective**: Embed Terraform execution inside Harness pipelines to dynamically provision cloud infrastructure (AWS S3, RDS, EKS Namespaces) before deploying application workloads.

### Project 17: Retail Banking Portal - GitOps Deployment with Harness GitOps & ArgoCD
* **Target Domain**: Retail Banking - Online Customer Web Application.
* **Tech Stack**: Harness GitOps, ArgoCD, GitHub, Kustomize, Kubernetes.
* **Objective**: Setup a production GitOps workflow using Harness GitOps (powered by ArgoCD). Sync declarative application states continuously from Git to target clusters.

### Project 18: Core Clearing House API - Advanced Looping (Matrix & Repeat Strategies)
* **Target Domain**: Interbank Settlement - Automated Clearing House (ACH) Processor.
* **Tech Stack**: Harness CI/CD, Matrix Execution, Multi-Region Deployment, Parallel Steps.
* **Objective**: Use Harness Matrix and Repeat strategies to build, test, and deploy a service simultaneously across 5 AWS regions and 3 Java runtime versions using a single pipeline definition.

### Project 19: ATM Fleet Monitoring System - Automated Self-Healing & Incident Remediation
* **Target Domain**: ATM Operations - Real-Time Terminal Monitoring Agent.
* **Tech Stack**: Harness SRM (Service Reliability Management), PagerDuty, Prometheus, Webhook Rollbacks.
* **Objective**: Connect Harness to production monitoring alerts. Automatically trigger automated rollback or remediation workflows when post-deployment CPU or memory leaks occur.

---

## 🔴 LEVEL 4: ENTERPRISE EXPERT (Governance, Compliance, Multi-Cloud & Mainframe Migration)

### Project 20: Regulatory Compliance Engine - Open Policy Agent (OPA) Guardrails (PCI-DSS & SOC2)
* **Target Domain**: Bank Compliance & Regulatory Reporting System.
* **Tech Stack**: Harness Policy as Code (OPA), Rego Language, Compliance Dashboards.
* **Objective**: Enforce enterprise compliance rules (e.g., "No deployment permitted without Snyk scan", "Prod releases require 2 VP approvals", "No root containers") using OPA policies in Harness.

### Project 21: Enterprise FinOps - Cloud Cost Management (CCM) & Auto-Stopping
* **Target Domain**: Enterprise Infrastructure & FinOps Group.
* **Tech Stack**: Harness CCM, AWS CloudWatch, Kubernetes Auto-Stopping.
* **Objective**: Implement Harness Cloud Cost Management. Track cloud cost allocation per dev team, set up budget alerts, and configure automated shutdown of non-prod EKS clusters on weekends.

### Project 22: Hybrid Banking Platform - Legacy VM (Ansible) & Modern K8s Coexistence
* **Target Domain**: Core Banking - Legacy Mainframe Wrapper & Modern Microservices.
* **Tech Stack**: Harness CD, Ansible Automation Platform, SSH/SSH-Win, Kubernetes.
* **Objective**: Build a hybrid pipeline that orchestrates legacy database updates on bare-metal Linux VMs via Ansible alongside modern microservice deployments on EKS.

### Project 23: Global Liquidity Management - Multi-Cloud Deployment (AWS + GCP Failover)
* **Target Domain**: Treasury & Cash Management - High-Availability FX Engine.
* **Tech Stack**: Harness CD, AWS EKS, Google Cloud GKE, Cross-Cloud Load Balancer.
* **Objective**: Deploy active-active banking microservices across both AWS EKS and GCP GKE with continuous global health monitoring and automated cloud-to-cloud failover.

### Project 24: Enterprise DevOps Acceleration - Dynamic Pipeline Templates & Shared Library
* **Target Domain**: Enterprise Architecture & Platform Engineering Group.
* **Tech Stack**: Harness Governance, Centralized YAML Templates, Custom Plugins.
* **Objective**: Design and publish golden-path pipeline templates for 500+ bank developers, allowing them to spin up compliant CI/CD pipelines in under 5 minutes with central administration.

### Project 25: The Master Capstone - End-to-End Enterprise Banking System Migration
* **Target Domain**: Core Banking Modernization - Instant Payment Processing Engine.
* **Tech Stack**: Complete Stack (GitHub, Harness CI/CD/SRM/CCM/OPA, AWS EKS, Helm, SonarQube, Snyk, Datadog, HashiCorp Vault, ArgoCD).
* **Objective**: Design, build, deploy, secure, govern, monitor, and troubleshoot a complete production-grade banking system from scratch using everything learned across the entire program.

---

## 📋 Rules of Our Apprenticeship:

1. **One Project at a Time**: We will complete every project thoroughly before moving to the next.
2. **20+ Years Expertise**: Every explanation will reflect real enterprise production standards, common traps, and interview scenarios.
3. **All 31 Required Sections**: Every project response will strictly follow the 31-part breakdown requested.
4. **No Placeholders or Shortcuts**: Complete scripts, manifests, YAMLs, and troubleshooting guides will be provided.

---

### 🚦 Next Step:
**Please review this 25-Project Master Roadmap.**  
Once you reply with your **approval**, we will immediately launch **Project 1: Core Banking Microservice - Basic CI Pipeline**.
