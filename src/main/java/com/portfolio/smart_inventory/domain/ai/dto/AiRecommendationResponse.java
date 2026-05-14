package com.portfolio.smart_inventory.domain.ai.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class AiRecommendationResponse {
    private String itemName;
    private Integer currentStock;
    private Integer safetyStock;
    private String recommendation;  // AI 추천 메시지
}