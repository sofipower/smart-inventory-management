package com.portfolio.smart_inventory.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;        // 품목명

    @Column(nullable = false)
    private Integer quantity;       // 발주수량

    @Column(nullable = false)
    private Integer unitPrice;      // 단가

    @Column(nullable = false)
    private String supplier;        // 납품업체

    @Column(nullable = false)
    private LocalDate orderDate;    // 발주일

    @Column(nullable = false)
    private LocalDate dueDate;      // 납기일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;     // 상태

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = OrderStatus.ORDERED;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String itemName, Integer quantity, Integer unitPrice,
                       String supplier, LocalDate dueDate, OrderStatus status) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
        this.dueDate = dueDate;
        this.status = status;
    }
}
