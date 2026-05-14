package com.portfolio.smart_inventory.domain.inventory.dto;

import com.portfolio.smart_inventory.domain.inventory.entity.Inventory;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class InventoryResponse {

    private Long id;
    private String itemName;
    private Integer currentStock;
    private Integer safetyStock;
    private String unit;
    private String supplier;
    private boolean belowSafetyStock;   // 안전재고 이하 여부
    private LocalDateTime createdAt;

    public static InventoryResponse from(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .itemName(inventory.getItemName())
                .currentStock(inventory.getCurrentStock())
                .safetyStock(inventory.getSafetyStock())
                .unit(inventory.getUnit())
                .supplier(inventory.getSupplier())
                .belowSafetyStock(inventory.isBelowSafetyStock())
                .createdAt(inventory.getCreatedAt())
                .build();
    }
}