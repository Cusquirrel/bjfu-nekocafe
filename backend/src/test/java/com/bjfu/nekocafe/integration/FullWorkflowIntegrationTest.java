package com.bjfu.nekocafe.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FullWorkflowIntegrationTest extends IntegrationTestSupport {
    @Test
    public void shouldCompleteReservePayReviewDashboardWorkflow() throws Exception {
        long reservationId = createReservation("it-full-workflow-001", 15, "16:00-18:00");
        String orderResponse = mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"reservationId\":" + reservationId + ",\"amountCents\":9900}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode order = mapper.readTree(orderResponse).path("data");

        mvc.perform(post("/api/orders/" + order.path("id").asLong() + "/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"channel\":\"MOCK_PAY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
        mvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"storeId\":1,\"rating\":5,\"content\":\"体验很好\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rating").value(5));
        mvc.perform(get("/api/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.revenueCents", greaterThanOrEqualTo(0)));
    }
}
