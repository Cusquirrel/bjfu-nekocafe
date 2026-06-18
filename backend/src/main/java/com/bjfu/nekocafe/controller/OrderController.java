package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final NekoCafeService service;
    public OrderController(NekoCafeService service) { this.service = service; }
    @PostMapping public ApiResponse<Map<String,Object>> create(@Valid @RequestBody CreateOrderRequest req) { return ApiResponse.ok(service.createOrder(req.userId, req.reservationId, req.amountCents)); }
    @PostMapping("/{id}/pay") public ApiResponse<Map<String,Object>> pay(@PathVariable Long id, @RequestBody(required=false) PayRequest req) { return ApiResponse.ok(service.payOrder(id, req == null ? "MOCK_PAY" : req.channel)); }
    public static class CreateOrderRequest { @NotNull public Long userId; public Long reservationId; @NotNull @Min(0) public Integer amountCents; }
    public static class PayRequest { public String channel; }
}
