---
name: code-review-skill
description: >
  Automated code review skill for Java Spring Boot microservices, Dockerfiles, and Harness CI/CD pipeline YAMLs. Use this skill whenever reviewing git commits, pull requests, or newly written code to enforce enterprise programming standards, security guidelines, unit test patterns, and concrete refactoring solutions.
---

# 🔍 Automated Code Review Skill & Enterprise Quality Standards

> **Skill Purpose**: This skill establishes an automated, standardized code review process. Whenever new code or git commits are submitted, this skill evaluates the diff against enterprise Java/Spring Boot standards, Docker security guidelines, and Harness CI/CD best practices—providing actionable comments, rationale, and exact code solutions.

---

## 📋 1. Code Review Workflow

When performing a code review on a git commit or pull request, follow this 4-step workflow:

1. **Diff Inspection**: Inspect modified files using `git diff HEAD~1` or view the full commit diff.
2. **Multi-Domain Standard Audit**:
   - Check **Java & Spring Boot Code Quality**
   - Check **Unit Test Coverage & Assertions**
   - Check **Dockerfile & Security Guardrails**
   - Check **Harness Pipeline YAML Best Practices**
3. **Issue Categorization**: Classify issues by severity:
   - 🔴 **CRITICAL**: Security vulnerability, build-breaking bug, hardcoded credential, or deployment blocker.
   - 🟡 **WARNING**: Non-standard code, missing unit test edge case, or inefficient pattern.
   - 🔵 **SUGGESTION**: Code style preference, minor optimization, or readability improvement.
4. **Structured Review Output**: For every flagged issue, provide:
   - **Location**: `File:LineNumber`
   - **Issue Summary**: What is wrong.
   - **Rationale**: Why it violates standards.
   - **Concrete Fix**: A drop-in refactored code solution.

---

## 📏 2. Enterprise Quality Checklists & Standards

### A. Java & Spring Boot Standards
- ❌ **No `System.out.println`**: Must use `org.slf4j.Logger` (e.g. `@Slf4j` or `LoggerFactory.getLogger()`).
- ❌ **No Hardcoded Credentials / Secrets**: Passwords, API tokens, and connection strings must be injected via `@Value("${...}")` or environment variables.
- ❌ **No Raw Map Responses for Complex Models**: Prefer typed DTOs (Data Transfer Objects) or `ResponseEntity<T>` with explicit HTTP status codes (`HttpStatus.OK`, `HttpStatus.BAD_REQUEST`).
- ❌ **No Silent Exception Swallowing**: Exception blocks must log errors or throw custom domain exceptions handled by `@RestControllerAdvice`.
- ✅ **API Versioning & REST Conventions**: Endpoints must include clear URI versioning (e.g. `/api/v1/auth/login`) and standard HTTP verbs (`GET`, `POST`, `PUT`, `DELETE`).

---

### B. Unit Testing Standards (JUnit 5 + MockMvc)
- ✅ **AAA Pattern**: Structure tests into **Arrange**, **Act**, **Assert**.
- ✅ **MockMvc Coverage**: Test both positive path (HTTP 200) and negative/failure paths (HTTP 400/401/500).
- ❌ **No Empty Assertions**: Every test method must contain explicit assertion matchers (e.g., `andExpect(jsonPath("$.status").value("UP"))`).

---

### C. Docker & Security Standards
- ✅ **Multi-Stage Build**: Separate build phase (`maven:3.9.6-builder`) from runtime phase (`eclipse-temurin:17-jre-alpine`).
- ✅ **Non-Root Execution**: Container process **must** run under a non-privileged user (`USER appuser`).
- ❌ **No `latest` Base Image Tags**: Base images must pin specific version tags (e.g. `17-jre-alpine` instead of `alpine`).
- ✅ **Layer Optimization**: Copy `pom.xml` and download dependencies before copying `src/` to maximize Docker cache utilization.

---

### D. Harness CI/CD Pipeline Standards
- ✅ **Monorepo Subfolder Pathing**: Always prefix paths with cloned repo name (`Harness-Projects/projects/...`).
- ✅ **Daemonless Kaniko Engine**: Use native `BuildAndPushDockerRegistry` step rather than `docker build` commands.
- ✅ **DockerHub Repository Format**: Must follow `<username>/<repo-name>` format (`naresh6961/auth-service`).
- ✅ **Semantic Tagging**: Tags must combine sequence ID with version prefix (`v2.0.<+pipeline.sequenceId>` and `latest`).
- ❌ **No Plaintext Secrets in Pipeline YAML**: Use Harness secret expressions (`<+secrets.getValue("dockerhub_password")>`).

---

## 💡 3. Code Review Example (Violation vs Solution)

### Example 1: Java Controller Code Review

#### ❌ Non-Standard / Bad Code:
```java
@PostMapping("/login")
public String login(@RequestBody Map<String, String> request) {
    System.out.println("Logging in user: " + request.get("username"));
    if (request.get("password").equals("admin123")) { // Hardcoded password + NullPointerException risk!
        return "OK";
    }
    return "FAIL";
}
```

#### 📍 Code Review Feedback:
* 🔴 **CRITICAL**: Hardcoded credential `"admin123"` detected in source code.
* 🟡 **WARNING**: `System.out.println` used instead of SLF4J logger.
* 🟡 **WARNING**: Potential `NullPointerException` if `username` or `password` keys are missing in payload.
* 🔵 **SUGGESTION**: Return structured `ResponseEntity` or DTO rather than raw String.

#### 🔧 Recommended Refactored Solution:
```java
package com.bank.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.getOrDefault("username", "");
        String password = request.getOrDefault("password", "");

        log.info("Processing authentication request for user: {}", username);

        if ("bankuser".equals(username) && "securepass123".equals(password)) {
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "message", "Login successful"
            ));
        }

        return ResponseEntity.status(401).body(Map.of(
            "authenticated", false,
            "message", "Invalid credentials"
        ));
    }
}
```

---

## 📌 4. Summary Review Template

When submitting code review feedback to a developer, use this standardized Markdown template:

```markdown
## 🔍 Code Review Summary for Commit `[commit-id]`

### 🟢 Status: [ Approved / Changes Requested ]

### 📋 Detailed Findings:
1. **`[FileName.java:LineNumber]`** — **[CRITICAL / WARNING / SUGGESTION]**
   - **Problem**: Description of standard violation.
   - **Rationale**: Why this impacts security, performance, or maintainability.
   - **Suggested Solution**:
     ```java
     // Refactored code snippet here
     ```
```
