package com.bank.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${auth.username:bankuser}")
    private String validUsername;

    @Value("${auth.password:securepass123}")
    private String validPassword;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Core-Banking-Auth-Service",
            "version", "1.0.1",
            "environment", "production"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.getOrDefault("username", "");
        String password = request.getOrDefault("password", "");

        log.info("Authentication attempt initiated for user: {}", username);

        if (validUsername.equals(username) && validPassword.equals(password)) {
            log.info("Authentication successful for user: {}", username);
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "token", "jwt-token-banking-auth-89324729384729384",
                "message", "Login successful"
            ));
        }

        log.warn("Authentication failed for user: {}", username);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
            "authenticated", false,
            "message", "Invalid credentials"
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
            "name", "Core Banking User Authentication API",
            "provider", "Retail Banking Division",
            "securityLevel", "PCI-DSS Compliant"
        ));
    }
}
