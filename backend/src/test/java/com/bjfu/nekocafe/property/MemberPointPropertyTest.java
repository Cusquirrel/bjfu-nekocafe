package com.bjfu.nekocafe.property;

import com.bjfu.nekocafe.service.MemberLevelPolicy;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.*;

public class MemberPointPropertyTest {
    private final MemberLevelPolicy policy = new MemberLevelPolicy();

    @Property
    public void pointsIncreaseShouldNeverDowngradeMemberLevel(@ForAll @IntRange(min = 0, max = 5000) int points,
                                                              @ForAll @IntRange(min = 0, max = 2000) int increment) {
        assertTrue(rank(policy.calculateLevel(points + increment)) >= rank(policy.calculateLevel(points)));
    }

    private int rank(String level) {
        if ("DIAMOND".equals(level)) return 4;
        if ("GOLD".equals(level)) return 3;
        if ("SILVER".equals(level)) return 2;
        return 1;
    }
}
