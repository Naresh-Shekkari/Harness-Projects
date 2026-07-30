---
name: harness-core-banking-ci
description: >
  Guide for building, testing, containerizing, and orchestrating a Harness CI
  pipeline for Java Spring Boot microservices. Use this skill whenever building
  Java microservices with Maven, writing multi-stage Dockerfiles, configuring Harness CI
  pipeline YAMLs, or troubleshooting GitHub webhook triggers and Kaniko image pushes.
---

# 🚀 Harness CI Pipeline Automation for Java Spring Boot Microservices

> **Skill Purpose**: This skill encapsulates the standard operating procedure (SOP), execution workflow, configuration patterns, and troubleshooting guardrails for automating Java Spring Boot microservice CI pipelines using Harness SaaS, DockerHub, and GitHub.

---

## 📌 1. Execution Workflow (Step-by-Step SOP)

### Phase 1: Local Code & Unit Test Verification
1. **Validate Maven Setup**: Ensure `pom.xml` contains Spring Boot Web starter and Spring Boot Test starter.
2. **Run Local Test Suite**: Execute `mvn clean test` to guarantee 100% of unit tests pass before pipeline integration.
3. **Validate REST Endpoints**: Verify `/health` and API endpoints return expected JSON payloads.

### Phase 2: Multi-Stage Containerization (Dockerfile)
1. **Builder Stage**: Use `maven:3.9.6-eclipse-temurin-17` to compile code and produce `.jar`.
2. **Runtime Stage**: Copy jar into lightweight `eclipse-temurin:17-jre-alpine` runtime image.
3. **Security Guardrail**: Enforce a non-root user (`appuser`) for enterprise banking compliance.
4. **Local Verification**: Test using `docker build -t auth-service:1.0 .` and `docker run -p 8080:8080 auth-service:1.0`.

### Phase 3: Harness SaaS Platform Setup
1. **Store Secrets**: Store DockerHub password and GitHub PAT in Harness Secrets Manager (`dockerhub_password`, `github_pat`).
2. **Configure Connectors**:
   - **GitHub Connector** (`github_connector`): Connects to repository with PAT credentials (`repo` and `admin:repo_hook` scopes).
   - **DockerHub Connector** (`dockerhub_connector`): Connects to Docker Registry.

### Phase 4: Harness CI Pipeline YAML Configuration
Construct `harness-ci-pipeline.yaml` with 3 core steps:
1. **`GitClone` Step**: Clones the GitHub repository explicitly onto branch `main`.
2. **`Maven_Build_and_Test` (Run Step)**: Runs `mvn clean test package` inside Maven container and publishes JUnit XML test reports (`target/surefire-reports/*.xml`).
3. **`Build_and_Push_to_DockerHub` (BuildAndPushDockerRegistry Step)**: Uses daemonless **Kaniko** engine to build container image and push immutable tags (`v2.0.<+pipeline.sequenceId>` and `latest`).

### Phase 5: GitOps Webhook Triggering
1. Commit code to repository (`git add .`, `git commit -m "..."`).
2. Push to GitHub (`git push origin main`).
3. Verify GitHub Webhook automatically triggers the Harness CI execution in real time.

---

## 🛠️ 2. Production Pipeline YAML Blueprint

```yaml
pipeline:
  name: Core_Banking_Auth_CI
  identifier: Core_Banking_Auth_CI
  projectIdentifier: default_project
  orgIdentifier: default
  tags:
    domain: banking
    service: auth-service
  stages:
    - stage:
        name: Build_Test_and_Push_Image
        identifier: Build_Test_and_Push_Image
        type: CI
        spec:
          cloneCodebase: false
          platform:
            os: Linux
            arch: Amd64
          runtime:
            type: Cloud
            spec: {}
          execution:
            steps:
              - step:
                  type: GitClone
                  name: Clone_Banking_Repository
                  identifier: Clone_Banking_Repository
                  spec:
                    connectorRef: github_connector
                    repoName: Harness-Projects
                    build:
                      type: branch
                      spec:
                        branch: main
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
                      cd Harness-Projects/projects/01-core-banking-ci
                      mvn clean test package
                    reports:
                      type: JUnit
                      spec:
                        paths:
                          - Harness-Projects/projects/01-core-banking-ci/target/surefire-reports/*.xml
              - step:
                  type: BuildAndPushDockerRegistry
                  name: Build_and_Push_to_DockerHub
                  identifier: Build_and_Push_to_DockerHub
                  spec:
                    connectorRef: dockerhub_connector
                    repo: naresh6961/auth-service
                    tags:
                      - v2.0.<+pipeline.sequenceId>
                      - latest
                    dockerfile: Harness-Projects/projects/01-core-banking-ci/Dockerfile
                    context: Harness-Projects/projects/01-core-banking-ci
```

---

## 🛡️ 3. Troubleshooting & Mandatory Guardrails

1. **Subfolder Relative Paths**: In monorepos/subfolders, always prefix step paths with the cloned repo folder name (e.g. `Harness-Projects/projects/01-core-banking-ci`).
2. **DockerHub Repo Format**: Always format `repo` as `<username>/<image-name>` (`naresh6961/auth-service`) to prevent `403 Forbidden` errors.
3. **Daemonless Container Builds**: Never run `docker build` in shell steps. Use Harness's native `BuildAndPushDockerRegistry` (Kaniko engine) for security and compatibility.
4. **Semantic Versioning**: Combine `<+pipeline.sequenceId>` with major/minor version prefixing (`v2.0.<+pipeline.sequenceId>`) for release tracking.
5. **Path Separators**: Always use Linux forward slashes (`/`) inside pipeline shell steps and configuration fields.
6. **Docker Metadata Load Cancellation (`ERROR [internal] load metadata for docker.io/...`)**: Caused by unauthenticated Docker daemon session rate limits or port 443 network blocks. Fix: Run `Test-NetConnection -ComputerName registry-1.docker.io -Port 443` (Step 1) and `docker login` (Step 2).

