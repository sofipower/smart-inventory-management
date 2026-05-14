package com.portfolio.smart_inventory.domain.inventory.service;

import com.portfolio.smart_inventory.domain.inventory.dto.InventoryRequest;
import com.portfolio.smart_inventory.domain.inventory.dto.InventoryResponse;
import com.portfolio.smart_inventory.domain.inventory.entity.Inventory;
import com.portfolio.smart_inventory.domain.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // 재고 등록
    @Transactional
    public InventoryResponse create(InventoryRequest request) {
        Inventory inventory = Inventory.builder()
                .itemName(request.getItemName())
                .currentStock(request.getCurrentStock())
                .safetyStock(request.getSafetyStock())
                .unit(request.getUnit())
                .supplier(request.getSupplier())
                .build();
        return InventoryResponse.from(inventoryRepository.save(inventory));
    }

    // 전체 조회
    public List<InventoryResponse> findAll() {
        return inventoryRepository.findAll().stream()
                .map(InventoryResponse::from)
                .toList();
    }

    // 단건 조회
    public InventoryResponse findById(Long id) {
        return InventoryResponse.from(getInventory(id));
    }

    // 안전재고 이하 품목 조회
    public List<InventoryResponse> findBelowSafetyStock() {
        List<Inventory> list = inventoryRepository.findBelowSafetyStock();
        if (!list.isEmpty()) {
            log.warn("[안전재고 알림] 재고 부족 품목 {}건 감지", list.size());
            list.forEach(i -> log.warn("  - 품목: {}, 현재재고: {}, 안전재고: {}",
                    i.getItemName(), i.getCurrentStock(), i.getSafetyStock()));
        }
        return list.stream()
                .map(InventoryResponse::from)
                .toList();
    }

    // 수정
    @Transactional
    public InventoryResponse update(Long id, InventoryRequest request) {
        Inventory inventory = getInventory(id);
        inventory.update(request.getCurrentStock(), request.getSafetyStock(),
                request.getUnit(), request.getSupplier());
        return InventoryResponse.from(inventory);
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        inventoryRepository.delete(getInventory(id));
    }

    private Inventory getInventory(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("재고 정보를 찾을 수 없습니다. id=" + id));
    }
}