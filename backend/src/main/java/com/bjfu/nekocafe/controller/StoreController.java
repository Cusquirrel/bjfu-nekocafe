package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    private final NekoCafeService service;
    public StoreController(NekoCafeService service) { this.service = service; }
    @GetMapping public ApiResponse<List<Map<String,Object>>> stores(@RequestParam(required=false) String city) { return ApiResponse.ok(service.stores(city)); }
    @GetMapping("/{storeId}/slots") public ApiResponse<List<Map<String,Object>>> slots(@PathVariable Long storeId, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date) { return ApiResponse.ok(service.slots(storeId, date)); }
}
