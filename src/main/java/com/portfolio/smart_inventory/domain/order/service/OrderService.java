package com.portfolio.smart_inventory.domain.order.service;

import com.portfolio.smart_inventory.domain.inventory.entity.Inventory;
import com.portfolio.smart_inventory.domain.inventory.repository.InventoryRepository;
import com.portfolio.smart_inventory.domain.order.dto.OrderRequest;
import com.portfolio.smart_inventory.domain.order.dto.OrderResponse;
import com.portfolio.smart_inventory.domain.order.entity.Order;
import com.portfolio.smart_inventory.domain.order.entity.OrderStatus;
import com.portfolio.smart_inventory.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    // 발주 등록
    @Transactional
    public OrderResponse create(OrderRequest request) {
        Inventory inventory = null;
        if (request.getInventoryId() != null) {
            inventory = inventoryRepository.findById(request.getInventoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "재고 정보를 찾을 수 없습니다. id=" + request.getInventoryId()));
        }

        Order order = Order.builder()
                .inventory(inventory)
                .itemName(request.getItemName())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .supplier(request.getSupplier())
                .orderDate(request.getOrderDate())
                .dueDate(request.getDueDate())
                .status(request.getStatus() != null ? request.getStatus() : OrderStatus.ORDERED)
                .build();

        return OrderResponse.from(orderRepository.save(order));
    }

    // 전체 조회
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    // 단건 조회
    public OrderResponse findById(Long id) {
        return OrderResponse.from(getOrder(id));
    }

    // 수정
    @Transactional
    public OrderResponse update(Long id, OrderRequest request) {
        Order order = getOrder(id);
        order.update(request.getItemName(), request.getQuantity(), request.getUnitPrice(),
                request.getSupplier(), request.getDueDate(), request.getStatus());
        return OrderResponse.from(order);
    }

    // 납품완료 처리 → 재고 자동 증가
    @Transactional
    public OrderResponse deliver(Long id) {
        Order order = getOrder(id);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("이미 납품완료된 발주입니다.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("취소된 발주는 납품완료 처리할 수 없습니다.");
        }

        // 재고 연동 여부 확인 후 증가
        if (order.getInventory() != null) {
            order.getInventory().increaseStock(order.getQuantity());
            log.info("[납품완료] 품목: {}, 재고 {}개 증가 → 현재재고: {}개",
                    order.getItemName(),
                    order.getQuantity(),
                    order.getInventory().getCurrentStock());
        } else {
            log.warn("[납품완료] 품목: {} → 연동된 재고 없음 (재고 자동 증가 안 됨)", order.getItemName());
        }

        order.updateStatus(OrderStatus.DELIVERED);
        return OrderResponse.from(order);
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        orderRepository.delete(getOrder(id));
    }

    private Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("발주 정보를 찾을 수 없습니다. id=" + id));
    }
}