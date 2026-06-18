package com.bjfu.nekocafe.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DashboardIntegrationTest extends IntegrationTestSupport {
    @Test
    public void shouldExposeOperationalDashboardMetrics() throws Exception {
        mvc.perform(get("/api/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reservations", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.completed", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.cancelled", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.watchCats", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.revenueCents", greaterThanOrEqualTo(0)));
    }
}
