package com.portfolio.smart_inventory.domain.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class InventoryRequest {

    @NotBlank(message = "품목명은 필수입니다.")
    private String itemName;

    @NotNull @Min(value = 0, message = "재고수량은 0 이상이어야 합니다.")
    private Integer currentStock;

    @NotNull @Min(value = 0, message = "안전재고는 0 이상이어야 합니다.")
    private Integer safetyStock;

    @NotBlank(message = "단위는 필수입니다.")
    private String unit;

    private String supplier;
}