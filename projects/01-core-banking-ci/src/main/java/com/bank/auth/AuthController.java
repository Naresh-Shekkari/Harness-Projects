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
            "version", "1.0.1",
            "environment", "production"
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

    @GetMapping("/info")
    public Map<String, String> getServiceInfo() {
        return Map.of(
            "name", "Core Banking User Authentication API",
            "provider", "Retail Banking Division",
            "securityLevel", "PCI-DSS Compliant"
        );
    }
}
