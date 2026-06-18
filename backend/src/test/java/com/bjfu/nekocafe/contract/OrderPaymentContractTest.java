package com.bjfu.nekocafe.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderPaymentContractTest {
    @Autowired private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void orderToPaymentContractShouldReturnPaidStatusAndChannel() throws Exception {
        String orderResponse = mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"amountCents\":6600}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andReturn().getResponse().getContentAsString();
        JsonNode order = mapper.readTree(orderResponse).path("data");

        mvc.perform(post("/api/orders/" + order.path("id").asLong() + "/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"channel\":\"MOCK_PAY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"))
                .andExpect(jsonPath("$.data.payment_channel").value("MOCK_PAY"));

        ContractReport.record("order", "payment", "pay created order through mock channel", "PASSED");
    }

    @AfterAll
    public static void writeReport() throws Exception {
        ContractReport.write();
    }
}
