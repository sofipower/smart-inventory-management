package com.portfolio.smart_inventory.domain.order.dto;

import com.portfolio.smart_inventory.domain.order.entity.Order;
import com.portfolio.smart_inventory.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private String itemName;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;     // quantity * unitPrice 계산값
    private String supplier;
    private LocalDate orderDate;
    private LocalDate dueDate;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .itemName(order.getItemName())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalPrice(order.getQuantity() * order.getUnitPrice())
                .supplier(order.getSupplier())
                .orderDate(order.getOrderDate())
                .dueDate(order.getDueDate())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}