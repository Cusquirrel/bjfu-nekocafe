package com.bjfu.nekocafe.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RecommendationIntegrationTest extends IntegrationTestSupport {
    @Test
    public void shouldReturnCatRecommendationsWithoutMenuKeywordRegression() throws Exception {
        mvc.perform(get("/api/recommendations/visit?userId=1&storeId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cats[0].interaction_status").value("AVAILABLE"))
                .andExpect(content().string(not(containsString("橘子套餐"))));
    }
}
