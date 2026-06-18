package com.bjfu.nekocafe.unit;

import com.bjfu.nekocafe.common.BusinessException;
import com.bjfu.nekocafe.service.NekoCafeService;
import com.bjfu.nekocafe.service.ReservationPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReservationServiceTest {
    @Autowired private NekoCafeService service;
    @Autowired private ReservationPolicy reservationPolicy;

    @Test
    public void shouldCreateReservationWithinTableCapacity() {
        Map<String, Object> reservation = service.createReservation(1L, 1L, LocalDate.now().plusDays(7),
                "10:00-12:00", 2, "unit-rsv-capacity-001");

        assertEquals("CONFIRMED", reservation.get("status"));
        assertEquals(2, ((Number) reservation.get("party_size")).intValue());
        assertTrue(reservationPolicy.canFitTable(2, 2));
    }

    @Test
    public void shouldRejectInvalidPartySize() {
        BusinessException ex = assertThrows(BusinessException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                service.createReservation(1L, 1L, LocalDate.now().plusDays(7),
                        "12:00-14:00", 7, "unit-rsv-invalid-size");
            }
        });
        assertEquals("INVALID_PARTY_SIZE", ex.getCode());
    }

    @Test
    public void cancelledReservationCannotBeCheckedIn() {
        Map<String, Object> reservation = service.createReservation(1L, 1L, LocalDate.now().plusDays(8),
                "14:00-16:00", 2, "unit-rsv-cancel-checkin");
        Long id = ((Number) reservation.get("id")).longValue();

        service.updateReservationStatus(id, "CANCELLED");

        BusinessException ex = assertThrows(BusinessException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                service.updateReservationStatus(id, "CHECKED_IN");
            }
        });
        assertEquals("STATUS_CLOSED", ex.getCode());
    }
}
