package com.bjfu.nekocafe.contract;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReservationApiContractTest {
    @Autowired private MockMvc mvc;

    @Test
    public void reservationToMemberContractShouldReturnMemberAwareReservation() throws Exception {
        String body = "{\"userId\":1,\"storeId\":1,\"visitDate\":\"" + LocalDate.now().plusDays(11)
                + "\",\"slot\":\"10:00-12:00\",\"partySize\":2,\"requestId\":\"contract-reservation-member\"}";

        mvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user_id").value(1))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.reservation_no", notNullValue()));

        ContractReport.record("reservation", "member", "create reservation awards member context", "PASSED");
    }

    @AfterAll
    public static void writeReport() throws Exception {
        ContractReport.write();
    }
}
