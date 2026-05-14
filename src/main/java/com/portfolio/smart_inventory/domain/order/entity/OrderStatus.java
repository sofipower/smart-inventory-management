package com.portfolio.smart_inventory.domain.order.entity;

public enum OrderStatus {
    ORDERED,    // 발주중
    DELIVERED,  // 납품완료
    OVERDUE,    // 미납
    CANCELLED   // 취소
}