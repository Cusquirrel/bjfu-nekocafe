package com.bjfu.nekocafe.unit;

import com.bjfu.nekocafe.common.BusinessException;
import com.bjfu.nekocafe.service.NekoCafeService;
import com.bjfu.nekocafe.service.OrderAmountPolicy;
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
public class OrderServiceTest {
    @Autowired private NekoCafeService service;
    @Autowired private OrderAmountPolicy orderAmountPolicy;

    @Test
    public void shouldCreateAndPayOrderWithMockPaymentChannel() {
        Map<String, Object> reservation = service.createReservation(1L, 1L, LocalDate.now().plusDays(10),
                "18:00-20:00", 2, "unit-order-pay-rsv");
        Map<String, Object> order = service.createOrder(1L, ((Number) reservation.get("id")).longValue(), 8800);

        Map<String, Object> paid = service.payOrder(((Number) order.get("id")).longValue(), null);

        assertEquals("PAID", paid.get("status"));
        assertEquals("MOCK_PAY", paid.get("payment_channel"));
    }

    @Test
    public void shouldRejectNegativeOrderAmount() {
        BusinessException ex = assertThrows(BusinessException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                service.createOrder(1L, null, -1);
            }
        });
        assertEquals("INVALID_AMOUNT", ex.getCode());
        assertFalse(orderAmountPolicy.isValidAmount(-1));
    }
}
