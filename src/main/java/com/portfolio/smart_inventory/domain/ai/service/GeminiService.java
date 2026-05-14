package com.portfolio.smart_inventory.domain.ai.service;

import com.portfolio.smart_inventory.domain.ai.dto.AiRecommendationResponse;
import com.portfolio.smart_inventory.domain.ai.dto.GeminiRequest;
import com.portfolio.smart_inventory.domain.inventory.entity.Inventory;
import com.portfolio.smart_inventory.domain.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final InventoryRepository inventoryRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    /**
     * 안전재고 이하 품목 전체에 대해 AI 발주 추천 메시지 생성
     */
    public List<AiRecommendationResponse> recommendForLowStock() {
        List<Inventory> lowStockList = inventoryRepository.findBelowSafetyStock();

        if (lowStockList.isEmpty()) {
            log.info("[AI 추천] 재고 부족 품목 없음");
            return List.of();
        }

        return lowStockList.stream()
                .map(this::generateRecommendation)
                .toList();
    }

    /**
     * 단일 품목 AI 발주 추천 메시지 생성
     */
    public AiRecommendationResponse recommendById(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("재고 정보를 찾을 수 없습니다. id=" + inventoryId));
        return generateRecommendation(inventory);
    }

    private AiRecommendationResponse generateRecommendation(Inventory inventory) {
        String prompt = String.format(
                "당신은 산업용 자재 구매 담당자입니다. 아래 재고 현황을 보고 발주 추천 메시지를 2~3문장으로 작성해주세요.\n\n" +
                        "품목명: %s\n" +
                        "현재 재고: %d%s\n" +
                        "안전재고 기준: %d%s\n" +
                        "주 납품업체: %s\n\n" +
                        "발주 수량, 긴급도, 납기 고려사항을 포함해서 실무적으로 작성해주세요.",
                inventory.getItemName(),
                inventory.getCurrentStock(), inventory.getUnit(),
                inventory.getSafetyStock(), inventory.getUnit(),
                inventory.getSupplier() != null ? inventory.getSupplier() : "미지정"
        );

        String recommendation = callGeminiApi(prompt);

        log.info("[AI 추천] 품목: {} → 추천 메시지 생성 완료", inventory.getItemName());

        return AiRecommendationResponse.builder()
                .itemName(inventory.getItemName())
                .currentStock(inventory.getCurrentStock())
                .safetyStock(inventory.getSafetyStock())
                .recommendation(recommendation)
                .build();
    }

    @SuppressWarnings("unchecked")
    private String callGeminiApi(String prompt) {
        try {
            RestClient restClient = RestClient.create();

            Map<?, ?> response = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .body(GeminiRequest.of(prompt))
                    .retrieve()
                    .body(Map.class);

            // 응답 파싱: candidates[0].content.parts[0].text
            List<Map<?, ?>> candidates = (List<Map<?, ?>>) response.get("candidates");
            Map<?, ?> content = (Map<?, ?>) candidates.get(0).get("content");
            List<Map<?, ?>> parts = (List<Map<?, ?>>) content.get("parts");
            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            log.error("[AI 추천] Gemini API 호출 실패 - {}", e.getMessage());
            return "AI 추천 메시지 생성에 실패했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}