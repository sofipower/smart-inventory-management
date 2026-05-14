package com.portfolio.smart_inventory.domain.inventory.controller;

import com.portfolio.smart_inventory.domain.inventory.dto.InventoryRequest;
import com.portfolio.smart_inventory.domain.inventory.dto.InventoryResponse;
import com.portfolio.smart_inventory.domain.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> findAll() {
        return ResponseEntity.ok(inventoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    // 안전재고 이하 품목 알림 조회
    @GetMapping("/alerts")
    public ResponseEntity<List<InventoryResponse>> findBelowSafetyStock() {
        return ResponseEntity.ok(inventoryService.findBelowSafetyStock());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}