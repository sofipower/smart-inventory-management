package com.portfolio.smart_inventory.domain.ai.controller;

import com.portfolio.smart_inventory.domain.ai.dto.AiRecommendationResponse;
import com.portfolio.smart_inventory.domain.ai.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;

    // 안전재고 이하 전체 품목 발주 추천
    @GetMapping("/recommendations")
    public ResponseEntity<List<AiRecommendationResponse>> recommendAll() {
        return ResponseEntity.ok(geminiService.recommendForLowStock());
    }

    // 특정 품목 발주 추천
    @GetMapping("/recommendations/{inventoryId}")
    public ResponseEntity<AiRecommendationResponse> recommendById(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(geminiService.recommendById(inventoryId));
    }
}