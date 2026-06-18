package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {
    private final NekoCafeService service;
    public HealthController(NekoCafeService service) { this.service = service; }
    @GetMapping("/health") public ApiResponse<Map<String,Object>> health() { return ApiResponse.ok(service.health()); }
}
