package com.portfolio.smart_inventory.domain.inventory.repository;

import com.portfolio.smart_inventory.domain.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 안전재고 이하 품목만 조회 (알림용)
    @Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.safetyStock")
    List<Inventory> findBelowSafetyStock();
}