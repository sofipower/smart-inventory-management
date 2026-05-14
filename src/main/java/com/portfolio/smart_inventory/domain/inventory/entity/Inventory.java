package com.portfolio.smart_inventory.domain.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String itemName;        // 품목명

    @Column(nullable = false)
    private Integer currentStock;   // 현재 재고수량

    @Column(nullable = false)
    private Integer safetyStock;    // 안전재고수량

    @Column(nullable = false)
    private String unit;            // 단위 (개, kg, m 등)

    private String supplier;        // 주 납품업체

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(Integer currentStock, Integer safetyStock,
                       String unit, String supplier) {
        this.currentStock = currentStock;
        this.safetyStock = safetyStock;
        this.unit = unit;
        this.supplier = supplier;
    }

    // 안전재고 이하 여부 체크
    public boolean isBelowSafetyStock() {
        return this.currentStock <= this.safetyStock;
    }
}