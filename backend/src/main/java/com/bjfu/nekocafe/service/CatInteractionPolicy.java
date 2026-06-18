package com.bjfu.nekocafe.service;

import org.springframework.stereotype.Component;

@Component
public class CatInteractionPolicy {
    public boolean isRecommendable(String interactionStatus, String healthStatus) {
        return "AVAILABLE".equals(interactionStatus)
                && ("NORMAL".equals(healthStatus) || "WATCH".equals(healthStatus));
    }
}
