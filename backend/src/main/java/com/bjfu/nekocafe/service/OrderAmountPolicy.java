package com.bjfu.nekocafe.service;

import org.springframework.stereotype.Component;

@Component
public class OrderAmountPolicy {
    public boolean isValidAmount(Integer amountCents) {
        return amountCents != null && amountCents >= 0;
    }

    public int payableAmountAfterDiscount(int amountCents, int discountCents) {
        int payable = amountCents - Math.max(0, discountCents);
        return Math.max(0, payable);
    }
}
