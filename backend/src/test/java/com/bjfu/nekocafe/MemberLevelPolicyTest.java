package com.bjfu.nekocafe;

import com.bjfu.nekocafe.service.MemberLevelPolicy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MemberLevelPolicyTest {
    private final MemberLevelPolicy policy = new MemberLevelPolicy();
    @Test public void shouldCalculateMemberLevelByPoints() {
        assertEquals("BRONZE", policy.calculateLevel(0));
        assertEquals("SILVER", policy.calculateLevel(300));
        assertEquals("GOLD", policy.calculateLevel(1000));
        assertEquals("DIAMOND", policy.calculateLevel(3000));
    }
    @Test public void pointsIncreaseShouldNotDowngradeLevel() {
        int[] samples = new int[]{0, 100, 299, 300, 999, 1000, 2999, 3000, 5000};
        for (int i = 0; i < samples.length - 1; i++) {
            assertTrue(rank(policy.calculateLevel(samples[i+1])) >= rank(policy.calculateLevel(samples[i])));
        }
    }
    private int rank(String level) {
        if ("DIAMOND".equals(level)) return 4;
        if ("GOLD".equals(level)) return 3;
        if ("SILVER".equals(level)) return 2;
        return 1;
    }
}
