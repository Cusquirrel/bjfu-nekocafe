package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final NekoCafeService service;
    public ReservationController(NekoCafeService service) { this.service = service; }
    @PostMapping public ApiResponse<Map<String,Object>> create(@Valid @RequestBody ReservationCreateRequest req) { return ApiResponse.ok(service.createReservation(req.userId, req.storeId, req.visitDate, req.slot, req.partySize, req.requestId)); }
    @GetMapping("/{id}") public ApiResponse<Map<String,Object>> get(@PathVariable Long id) { return ApiResponse.ok(service.reservation(id)); }
    @PostMapping("/{id}/cancel") public ApiResponse<Map<String,Object>> cancel(@PathVariable Long id) { return ApiResponse.ok(service.updateReservationStatus(id, "CANCELLED")); }
    @PostMapping("/{id}/check-in") public ApiResponse<Map<String,Object>> checkIn(@PathVariable Long id) { return ApiResponse.ok(service.updateReservationStatus(id, "CHECKED_IN")); }
    @PostMapping("/{id}/complete") public ApiResponse<Map<String,Object>> complete(@PathVariable Long id) { return ApiResponse.ok(service.updateReservationStatus(id, "COMPLETED")); }
    public static class ReservationCreateRequest {
        @NotNull public Long userId;
        @NotNull public Long storeId;
        @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) public LocalDate visitDate;
        @NotBlank public String slot;
        @NotNull @Min(1) @Max(6) public Integer partySize;
        @NotBlank public String requestId;
    }
}
