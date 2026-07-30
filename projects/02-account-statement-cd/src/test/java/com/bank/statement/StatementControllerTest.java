package com.bank.statement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/statements/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Customer-Account-Statement-Service"));
    }

    @Test
    public void testGetAccountStatement() throws Exception {
        mockMvc.perform(get("/api/v1/statements/ACCT-889922"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACCT-889922"))
                .andExpect(jsonPath("$.customerName").value("Jane Doe"))
                .andExpect(jsonPath("$.accountType").value("Savings"));
    }
}
