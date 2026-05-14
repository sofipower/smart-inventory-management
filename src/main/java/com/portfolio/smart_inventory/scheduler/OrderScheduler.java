package com.portfolio.smart_inventory.scheduler;

import com.portfolio.smart_inventory.domain.order.entity.Order;
import com.portfolio.smart_inventory.domain.order.entity.OrderStatus;
import com.portfolio.smart_inventory.domain.order.repository.OrderRepository;
import com.portfolio.smart_inventory.domain.inventory.entity.Inventory;
import com.portfolio.smart_inventory.domain.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * 매일 오전 9시 — 미납 자동 체크
     * 납기일이 오늘 이전인데 ORDERED 상태인 발주 → OVERDUE로 변경
     */

    // 테스트용 (10초마다 실행)
    //@Scheduled(cron = "0/10 * * * * *")
    // 실제 운영 (매일 오전 9시)
    @Scheduled(cron = "0 0 9 * * *")

    @Transactional
    public void checkOverdueOrders() {
        log.info("[Scheduler] 미납 체크 시작 - {}", LocalDateTime.now());

        List<Order> overdueList = orderRepository.findByDueDateBeforeAndStatus(
                LocalDate.now(), OrderStatus.ORDERED
        );

        if (overdueList.isEmpty()) {
            log.info("[Scheduler] 미납 발주 없음");
            return;
        }

        overdueList.forEach(order -> {
            order.update(
                    order.getItemName(),
                    order.getQuantity(),
                    order.getUnitPrice(),
                    order.getSupplier(),
                    order.getDueDate(),
                    OrderStatus.OVERDUE
            );
            log.warn("[Scheduler] 미납 처리 - 품목: {}, 납기일: {}, 업체: {}",
                    order.getItemName(), order.getDueDate(), order.getSupplier());
        });

        log.info("[Scheduler] 미납 처리 완료 - 총 {}건", overdueList.size());
    }

    /**
     * 매일 오전 9시 — 안전재고 이하 품목 경고 로그
     */

    // 테스트용 (10초마다 실행)
    //@Scheduled(cron = "0/10 * * * * *")

    // 실제 운영 (매일 오전 9시)
    @Scheduled(cron = "0 0 9 * * *")
    public void checkSafetyStock() {
        log.info("[Scheduler] 안전재고 체크 시작 - {}", LocalDateTime.now());

        List<Inventory> alertList = inventoryRepository.findBelowSafetyStock();

        if (alertList.isEmpty()) {
            log.info("[Scheduler] 안전재고 이하 품목 없음");
            return;
        }

        alertList.forEach(i ->
                log.warn("[Scheduler] 재고 부족 - 품목: {}, 현재: {}{}  안전재고: {}{}",
                        i.getItemName(),
                        i.getCurrentStock(), i.getUnit(),
                        i.getSafetyStock(), i.getUnit())
        );

        log.info("[Scheduler] 안전재고 체크 완료 - 부족 품목 {}건", alertList.size());
    }
}