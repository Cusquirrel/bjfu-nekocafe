package com.bjfu.nekocafe;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationFlowIntegrationTest {
    @Autowired private MockMvc mvc;

    @Test public void shouldCreateAndCancelReservation() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String body = "{\"userId\":1,\"storeId\":1,\"visitDate\":\"" + tomorrow + "\",\"slot\":\"10:00-12:00\",\"partySize\":2,\"requestId\":\"it-create-001\"}";
        String response = mvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andReturn().getResponse().getContentAsString();
        long id = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).path("data").path("id").asLong();
        mvc.perform(post("/api/reservations/" + id + "/cancel"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test public void duplicateRequestIdShouldBeRejected() throws Exception {
        String tomorrow = LocalDate.now().plusDays(2).toString();
        String body = "{\"userId\":1,\"storeId\":1,\"visitDate\":\"" + tomorrow + "\",\"slot\":\"12:00-14:00\",\"partySize\":2,\"requestId\":\"it-dup-001\"}";
        mvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        mvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("DUPLICATE_REQUEST"));
    }

    @Test public void shouldExposeHealthAndDashboard() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("UP"));
        mvc.perform(get("/api/dashboard/overview")).andExpect(status().isOk()).andExpect(jsonPath("$.data.reservations", greaterThanOrEqualTo(0)));
    }
}
