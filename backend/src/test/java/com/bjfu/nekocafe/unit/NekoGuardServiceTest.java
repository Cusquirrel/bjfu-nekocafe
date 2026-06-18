package com.bjfu.nekocafe.unit;

import com.bjfu.nekocafe.service.CatInteractionPolicy;
import com.bjfu.nekocafe.service.RbacPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NekoGuardServiceTest {
    private final CatInteractionPolicy catPolicy = new CatInteractionPolicy();
    private final RbacPolicy rbacPolicy = new RbacPolicy();

    @Test
    public void restingOrIsolatedCatsMustNotBeRecommendedForInteraction() {
        assertFalse(catPolicy.isRecommendable("RESTING", "NORMAL"));
        assertFalse(catPolicy.isRecommendable("ISOLATED", "NORMAL"));
        assertFalse(catPolicy.isRecommendable("AVAILABLE", "SICK"));
    }

    @Test
    public void shouldEnforceRoleBasedActionBoundaries() {
        assertTrue(rbacPolicy.isAllowed("STAFF", "RESERVATION_CHECK_IN"));
        assertFalse(rbacPolicy.isAllowed("CUSTOMER", "DASHBOARD_READ"));
        assertTrue(rbacPolicy.isAllowed("CAT_KEEPER", "CAT_HEALTH_RECORD"));
        assertTrue(rbacPolicy.isAllowed("OPS_MANAGER", "SECURITY_AUDIT_READ"));
    }
}
