package com.bjfu.nekocafe.service;

import org.springframework.stereotype.Component;

@Component
public class RecommendationKeywordPolicy {
    public enum Intent {
        CAT_PROFILE,
        MENU_PACKAGE,
        UNKNOWN
    }

    public Intent classify(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Intent.UNKNOWN;
        }
        String normalized = keyword.trim().toLowerCase();
        if (normalized.contains("橘猫") || normalized.contains("orange cat")) {
            return Intent.CAT_PROFILE;
        }
        if (normalized.contains("橘子套餐") || normalized.contains("orange package") || normalized.contains("orange menu")) {
            return Intent.MENU_PACKAGE;
        }
        if (normalized.contains("猫") || normalized.contains("cat")) {
            return Intent.CAT_PROFILE;
        }
        return Intent.UNKNOWN;
    }

    public boolean shouldRecommendMenuPackage(String keyword, String packageName) {
        return classify(keyword) == Intent.MENU_PACKAGE
                && packageName != null
                && packageName.contains("橘子套餐");
    }
}
