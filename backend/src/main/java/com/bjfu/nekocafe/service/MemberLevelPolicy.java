package com.bjfu.nekocafe.service;

import org.springframework.stereotype.Component;

@Component
public class MemberLevelPolicy {
    public String calculateLevel(int points) {
        if (points >= 3000) return "DIAMOND";
        if (points >= 1000) return "GOLD";
        if (points >= 300) return "SILVER";
        return "BRONZE";
    }
}
