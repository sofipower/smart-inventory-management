package com.portfolio.smart_inventory.domain.order.repository;

import com.portfolio.smart_inventory.domain.order.entity.Order;
import com.portfolio.smart_inventory.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    // 납기일이 지났는데 미납인 건 조회 (Scheduler에서 사용 예정)
    List<Order> findByDueDateBeforeAndStatus(LocalDate date, OrderStatus status);
}