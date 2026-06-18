package com.bjfu.nekocafe.unit;

import com.bjfu.nekocafe.service.CatInteractionPolicy;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CatServiceTest {
    @Autowired private NekoCafeService service;
    @Autowired private CatInteractionPolicy catInteractionPolicy;

    @Test
    public void shouldRecordCatHealthAndChangeInteractionStatus() {
        Map<String, Object> cat = service.addCatHealth(1L, "INTERACTION_STATUS", "RESTING", "catkeeper");

        assertEquals("RESTING", cat.get("interaction_status"));
        assertFalse(catInteractionPolicy.isRecommendable("RESTING", "NORMAL"));
    }

    @Test
    public void shouldKeepWatchCatRecommendableOnlyWhenAvailable() {
        assertTrue(catInteractionPolicy.isRecommendable("AVAILABLE", "WATCH"));
        assertFalse(catInteractionPolicy.isRecommendable("ISOLATED", "WATCH"));
    }
}
