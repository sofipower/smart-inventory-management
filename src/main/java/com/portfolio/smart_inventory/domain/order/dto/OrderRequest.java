package com.portfolio.smart_inventory.domain.order.dto;

import com.portfolio.smart_inventory.domain.order.entity.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class OrderRequest {

    private Long inventoryId;       // 연동할 재고 ID (선택)

    @NotBlank(message = "품목명은 필수입니다.")
    private String itemName;

    @NotNull @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private Integer quantity;

    @NotNull @Min(value = 0, message = "단가는 0 이상이어야 합니다.")
    private Integer unitPrice;

    @NotBlank(message = "납품업체는 필수입니다.")
    private String supplier;

    @NotNull(message = "발주일은 필수입니다.")
    private LocalDate orderDate;

    @NotNull(message = "납기일은 필수입니다.")
    private LocalDate dueDate;

    private OrderStatus status;
}