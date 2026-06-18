package com.bjfu.nekocafe.service;

import org.springframework.stereotype.Component;

@Component
public class RbacPolicy {
    public boolean isAllowed(String roleCode, String action) {
        if ("OPS_MANAGER".equals(roleCode)) {
            return true;
        }
        if ("STAFF".equals(roleCode)) {
            return action != null && (action.startsWith("RESERVATION_") || "DASHBOARD_READ".equals(action));
        }
        if ("CAT_KEEPER".equals(roleCode)) {
            return action != null && action.startsWith("CAT_");
        }
        if ("CUSTOMER".equals(roleCode)) {
            return action != null && (action.startsWith("RESERVATION_SELF_") || action.startsWith("ORDER_SELF_"));
        }
        return false;
    }
}
