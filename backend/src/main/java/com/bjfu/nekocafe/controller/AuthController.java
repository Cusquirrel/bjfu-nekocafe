package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final NekoCafeService service;
    public AuthController(NekoCafeService service) { this.service = service; }
    @PostMapping("/register") public ApiResponse<Map<String,Object>> register(@Valid @RequestBody RegisterRequest req) { return ApiResponse.ok(service.register(req.username, req.phone, req.password)); }
    @PostMapping("/login") public ApiResponse<Map<String,Object>> login(@Valid @RequestBody LoginRequest req) { return ApiResponse.ok(service.login(req.username, req.password)); }
    public static class RegisterRequest { @NotBlank public String username; public String phone; @NotBlank public String password; }
    public static class LoginRequest { @NotBlank public String username; @NotBlank public String password; }
}
