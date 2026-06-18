package com.bjfu.nekocafe.unit;

import com.bjfu.nekocafe.service.NekoCafeService;
import com.bjfu.nekocafe.service.RecommendationKeywordPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RecommendationServiceTest {
    @Autowired private NekoCafeService service;
    @Autowired private RecommendationKeywordPolicy keywordPolicy;

    @Test
    public void shouldRecommendAvailableCatsOnly() {
        Map<String, Object> recommendation = service.recommendation(1L, 1L);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cats = (List<Map<String, Object>>) recommendation.get("cats");
        assertFalse(cats.isEmpty());
        for (Map<String, Object> cat : cats) {
            assertEquals("AVAILABLE", cat.get("interaction_status"));
        }
    }

    @Test
    public void shouldNotTreatOrangeCatAsOrangeMenuPackage() {
        assertEquals(RecommendationKeywordPolicy.Intent.CAT_PROFILE, keywordPolicy.classify("我想看看橘猫"));
        assertFalse(keywordPolicy.shouldRecommendMenuPackage("橘猫", "橘子套餐"));
    }
}
