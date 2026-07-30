package com.bank.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statements")
public class StatementController {

    private static final Logger log = LoggerFactory.getLogger(StatementController.class);

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        log.info("Liveness/Readiness probe health check request received.");
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Customer-Account-Statement-Service",
            "version", "1.0.0",
            "environment", "Kubernetes-Deployment"
        ));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Map<String, Object>> getStatement(@PathVariable String accountNumber) {
        log.info("Fetching customer account statement for account: {}", accountNumber);

        List<Map<String, Object>> transactions = List.of(
            Map.of("id", "TXN-9012", "date", "2026-07-28", "description", "ATM Withdrawal", "amount", -200.00, "currency", "USD"),
            Map.of("id", "TXN-9013", "date", "2026-07-29", "description", "Salary Credit", "amount", 5500.00, "currency", "USD"),
            Map.of("id", "TXN-9014", "date", "2026-07-30", "description", "POS Merchant Payment", "amount", -45.50, "currency", "USD")
        );

        return ResponseEntity.ok(Map.of(
            "accountNumber", accountNumber,
            "customerName", "Jane Doe",
            "accountType", "Savings",
            "currentBalance", 12854.50,
            "currency", "USD",
            "recentTransactions", transactions
        ));
    }
}
