package com.bjfu.nekocafe.property;

import com.bjfu.nekocafe.service.CatInteractionPolicy;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.junit.jupiter.api.Assertions.*;

public class CatStatusPropertyTest {
    private final CatInteractionPolicy policy = new CatInteractionPolicy();

    @Property
    public void restingOrIsolatedCatsShouldNeverBeInteractive(@ForAll("blockedStatuses") String interactionStatus,
                                                              @ForAll("healthStatuses") String healthStatus) {
        assertFalse(policy.isRecommendable(interactionStatus, healthStatus));
    }

    @Provide
    Arbitrary<String> blockedStatuses() {
        return Arbitraries.of("RESTING", "ISOLATED");
    }

    @Provide
    Arbitrary<String> healthStatuses() {
        return Arbitraries.of("NORMAL", "WATCH", "SICK");
    }
}
