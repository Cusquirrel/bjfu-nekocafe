package com.bjfu.nekocafe.controller;

import com.bjfu.nekocafe.common.ApiResponse;
import com.bjfu.nekocafe.service.NekoCafeService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final NekoCafeService service;

    public ReviewController(NekoCafeService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody CreateReviewRequest req) {
        return ApiResponse.ok(service.createReview(req.userId, req.storeId, req.rating, req.content));
    }

    public static class CreateReviewRequest {
        @NotNull public Long userId;
        @NotNull public Long storeId;
        @NotNull @Min(1) @Max(5) public Integer rating;
        public String content;
    }
}
