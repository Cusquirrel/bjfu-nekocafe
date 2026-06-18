package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final NekoCafeService service;
    public DashboardController(NekoCafeService service) { this.service = service; }
    @GetMapping("/overview") public ApiResponse<Map<String,Object>> overview() { return ApiResponse.ok(service.dashboard()); }
}
