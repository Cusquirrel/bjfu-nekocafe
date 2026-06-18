package com.bjfu.nekocafe.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class IntegrationTestSupport {
    @Autowired protected MockMvc mvc;
    protected final ObjectMapper mapper = new ObjectMapper();

    protected long createReservation(String requestId, int plusDays, String slot) throws Exception {
        String body = "{\"userId\":1,\"storeId\":1,\"visitDate\":\"" + LocalDate.now().plusDays(plusDays)
                + "\",\"slot\":\"" + slot + "\",\"partySize\":2,\"requestId\":\"" + requestId + "\"}";
        String response = mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode data = mapper.readTree(response).path("data");
        return data.path("id").asLong();
    }
}
