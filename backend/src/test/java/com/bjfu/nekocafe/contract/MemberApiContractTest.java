package com.bjfu.nekocafe.contract;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MemberApiContractTest {
    @Autowired private MockMvc mvc;

    @Test
    public void memberProfileContractShouldExposeLevelAndPoints() throws Exception {
        mvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("demo_member"))
                .andExpect(jsonPath("$.data.level_code").value("GOLD"))
                .andExpect(jsonPath("$.data.points", greaterThanOrEqualTo(0)));

        ContractReport.record("reservation", "member", "query member level and points", "PASSED");
    }

    @AfterAll
    public static void writeReport() throws Exception {
        ContractReport.write();
    }
}
