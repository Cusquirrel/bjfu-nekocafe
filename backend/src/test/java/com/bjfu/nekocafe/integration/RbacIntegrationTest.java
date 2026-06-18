package com.bjfu.nekocafe.integration;

import com.bjfu.nekocafe.service.RbacPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RbacIntegrationTest extends IntegrationTestSupport {
    @Autowired private RbacPolicy rbacPolicy;
    @Autowired private JdbcTemplate jdbc;

    @Test
    public void shouldRecordAuditWhenPrivilegedReservationStatusChanges() throws Exception {
        long id = createReservation("it-rbac-audit-001", 14, "14:00-16:00");
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/reservations/" + id + "/check-in"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());

        Integer auditCount = jdbc.queryForObject("SELECT COUNT(*) FROM audit_logs WHERE action='UPDATE_RESERVATION_STATUS' AND target_id=?",
                Integer.class, String.valueOf(id));
        assertTrue(auditCount.intValue() >= 1);
        assertTrue(rbacPolicy.isAllowed("STAFF", "RESERVATION_CHECK_IN"));
        assertFalse(rbacPolicy.isAllowed("CUSTOMER", "DASHBOARD_READ"));
    }
}
