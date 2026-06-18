package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cats")
public class CatController {
    private final NekoCafeService service;
    public CatController(NekoCafeService service) { this.service = service; }
    @GetMapping public ApiResponse<List<Map<String,Object>>> cats(@RequestParam(required=false) Long storeId) { return ApiResponse.ok(service.cats(storeId)); }
    @PostMapping("/{catId}/health-records") public ApiResponse<Map<String,Object>> health(@PathVariable Long catId, @Valid @RequestBody CatHealthRequest req) { return ApiResponse.ok(service.addCatHealth(catId, req.recordType, req.value, req.recordedBy)); }
    public static class CatHealthRequest { @NotBlank public String recordType; @NotBlank public String value; public String recordedBy; }
}
