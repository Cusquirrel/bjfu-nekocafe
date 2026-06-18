package com.bjfu.nekocafe.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReservationIntegrationTest extends IntegrationTestSupport {
    @Test
    public void shouldCreateCheckInAndCompleteReservation() throws Exception {
        long id = createReservation("it-reservation-flow-001", 12, "10:00-12:00");

        mvc.perform(post("/api/reservations/" + id + "/check-in"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CHECKED_IN"));
        mvc.perform(post("/api/reservations/" + id + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}
