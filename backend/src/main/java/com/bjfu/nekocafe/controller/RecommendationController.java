package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final NekoCafeService service;
    public RecommendationController(NekoCafeService service) { this.service = service; }
    @GetMapping("/visit") public ApiResponse<Map<String,Object>> visit(@RequestParam Long userId, @RequestParam Long storeId) { return ApiResponse.ok(service.recommendation(userId, storeId)); }
}
