package com.bjfu.nekocafe.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PaymentIntegrationTest extends IntegrationTestSupport {
    @Test
    public void shouldPayCreatedOrderAndRejectSecondPayment() throws Exception {
        String response = mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amountCents\":5200}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode order = mapper.readTree(response).path("data");

        mvc.perform(post("/api/orders/" + order.path("id").asLong() + "/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"channel\":\"MOCK_PAY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
        mvc.perform(post("/api/orders/" + order.path("id").asLong() + "/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"channel\":\"MOCK_PAY\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORDER_STATUS_ERROR"));
    }
}
