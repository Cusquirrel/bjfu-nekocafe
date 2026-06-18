package com.bjfu.nekocafe.property;

import com.bjfu.nekocafe.service.OrderAmountPolicy;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.*;

public class OrderAmountPropertyTest {
    private final OrderAmountPolicy policy = new OrderAmountPolicy();

    @Property
    public void payableAmountShouldNeverBeNegative(@ForAll @IntRange(min = 0, max = 200000) int amountCents,
                                                   @ForAll @IntRange(min = 0, max = 300000) int discountCents) {
        int payable = policy.payableAmountAfterDiscount(amountCents, discountCents);
        assertTrue(payable >= 0);
        assertTrue(payable <= amountCents);
    }
}
