package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final NekoCafeService service;
    public MemberController(NekoCafeService service) { this.service = service; }
    @GetMapping("/{userId}") public ApiResponse<Map<String,Object>> member(@PathVariable Long userId) { return ApiResponse.ok(service.userProfile(userId)); }
}
