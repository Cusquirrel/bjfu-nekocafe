package com.bjfu.nekocafe.property;

import com.bjfu.nekocafe.service.RecommendationKeywordPolicy;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationKeywordPropertyTest {
    private final RecommendationKeywordPolicy policy = new RecommendationKeywordPolicy();

    @Property
    public void orangeCatKeywordShouldAlwaysMapToCatProfile(@ForAll("shortText") String prefix,
                                                            @ForAll("shortText") String suffix) {
        String keyword = prefix + "橘猫" + suffix;
        assertEquals(RecommendationKeywordPolicy.Intent.CAT_PROFILE, policy.classify(keyword));
        assertFalse(policy.shouldRecommendMenuPackage(keyword, "橘子套餐"));
    }

    @Provide
    Arbitrary<String> shortText() {
        return Arbitraries.strings().alpha().ofMinLength(0).ofMaxLength(8);
    }
}
