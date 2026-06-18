package com.bjfu.nekocafe.contract;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CatApiContractTest {
    @Autowired private MockMvc mvc;

    @Test
    public void reservationToCatContractShouldExposeInteractionAndHealthStatus() throws Exception {
        mvc.perform(get("/api/cats?storeId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThan(0)))
                .andExpect(jsonPath("$.data[0].interaction_status").exists())
                .andExpect(jsonPath("$.data[0].health_status").exists());

        ContractReport.record("reservation", "cat", "query cat interaction and health constraints", "PASSED");
    }

    @AfterAll
    public static void writeReport() throws Exception {
        ContractReport.write();
    }
}
