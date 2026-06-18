package com.bjfu.nekocafe.unit;

import com.bjfu.nekocafe.service.NekoCafeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MemberServiceTest {
    @Autowired private NekoCafeService service;
    @Autowired private JdbcTemplate jdbc;

    @Test
    public void shouldRegisterMemberWithBronzeLevelAndAuditLog() {
        Map<String, Object> profile = service.register("unit_member_001", "13900001111", "pwd");

        assertEquals("unit_member_001", profile.get("username"));
        assertEquals("BRONZE", profile.get("level_code"));
        Integer auditCount = jdbc.queryForObject("SELECT COUNT(*) FROM audit_logs WHERE action='REGISTER' AND actor='unit_member_001'", Integer.class);
        assertEquals(1, auditCount.intValue());
    }

    @Test
    public void reservationShouldAccumulateMemberPointsWithoutDowngrade() {
        int before = service.memberPoints(1L);
        Map<String, Object> beforeProfile = service.userProfile(1L);

        service.createReservation(1L, 1L, LocalDate.now().plusDays(9),
                "16:00-18:00", 2, "unit-member-points");

        Map<String, Object> afterProfile = service.userProfile(1L);
        assertEquals(before + 20, service.memberPoints(1L));
        assertEquals(beforeProfile.get("level_code"), afterProfile.get("level_code"));
    }
}
