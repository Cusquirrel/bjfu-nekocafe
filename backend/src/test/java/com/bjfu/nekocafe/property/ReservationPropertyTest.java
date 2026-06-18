package com.bjfu.nekocafe.property;

import com.bjfu.nekocafe.service.ReservationPolicy;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationPropertyTest {
    private final ReservationPolicy policy = new ReservationPolicy();

    @Property
    public void anyLegalPartySizeShouldBeValid(@ForAll @IntRange(min = 1, max = 6) int partySize) {
        assertTrue(policy.isValidPartySize(partySize));
    }

    @Property
    public void tableCapacityRuleShouldMatchPartySize(@ForAll @IntRange(min = 1, max = 8) int partySize,
                                                      @ForAll @IntRange(min = 1, max = 8) int capacity) {
        assertEquals(partySize <= capacity, policy.canFitTable(partySize, capacity));
    }
}
