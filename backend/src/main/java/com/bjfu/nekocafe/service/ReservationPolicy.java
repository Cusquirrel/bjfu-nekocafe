package com.bjfu.nekocafe.service;

import org.springframework.stereotype.Component;

@Component
public class ReservationPolicy {
    public boolean isValidPartySize(Integer partySize) {
        return partySize != null && partySize >= 1 && partySize <= 6;
    }

    public boolean canFitTable(int partySize, int capacity) {
        return partySize >= 1 && partySize <= capacity;
    }

    public boolean canChangeStatus(String currentStatus) {
        return !"CANCELLED".equals(currentStatus) && !"COMPLETED".equals(currentStatus);
    }

    public boolean canCheckIn(String currentStatus) {
        return "CONFIRMED".equals(currentStatus);
    }

    public boolean isSupportedStatus(String targetStatus) {
        return "CONFIRMED".equals(targetStatus)
                || "CHECKED_IN".equals(targetStatus)
                || "CANCELLED".equals(targetStatus)
                || "COMPLETED".equals(targetStatus);
    }
}
