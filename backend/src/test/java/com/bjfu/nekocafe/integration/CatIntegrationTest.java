package com.bjfu.nekocafe.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CatIntegrationTest extends IntegrationTestSupport {
    @Test
    public void shouldSubmitHealthRecordAndExposeCatStatus() throws Exception {
        mvc.perform(post("/api/cats/1/health-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recordType\":\"HEALTH_STATUS\",\"value\":\"WATCH\",\"recordedBy\":\"catkeeper\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.health_status").value("WATCH"));

        mvc.perform(get("/api/cats?storeId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThan(0)));
    }
}
