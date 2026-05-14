package com.portfolio.smart_inventory.domain.order.service;

import com.portfolio.smart_inventory.domain.order.dto.OrderRequest;
import com.portfolio.smart_inventory.domain.order.dto.OrderResponse;
import com.portfolio.smart_inventory.domain.order.entity.Order;
import com.portfolio.smart_inventory.domain.order.entity.OrderStatus;
import com.portfolio.smart_inventory.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // 발주 등록
    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order order = Order.builder()
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