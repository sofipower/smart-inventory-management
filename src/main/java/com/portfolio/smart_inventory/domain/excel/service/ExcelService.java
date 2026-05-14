package com.portfolio.smart_inventory.domain.excel.service;

import com.portfolio.smart_inventory.domain.order.entity.Order;
import com.portfolio.smart_inventory.domain.order.entity.OrderStatus;
import com.portfolio.smart_inventory.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final OrderRepository orderRepository;

    /**
     * 엑셀 파일 파싱 → 발주 데이터 일괄 저장
     * 엑셀 컬럼 순서: 품목명 | 수량 | 단가 | 납품업체 | 발주일 | 납기일
     */
    @Transactional
    public int importOrders(MultipartFile file) throws IOException {
        List<Order> orders = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 1행은 헤더 → 2행부터 읽기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Order order = Order.builder()
                            .itemName(getCellValue(row.getCell(0)))
                            .quantity(Integer.parseInt(getCellValue(row.getCell(1))))
                            .unitPrice(Integer.parseInt(getCellValue(row.getCell(2))))
                            .supplier(getCellValue(row.getCell(3)))
                            .orderDate(parseDate(getCellValue(row.getCell(4))))
                            .dueDate(parseDate(getCellValue(row.getCell(5))))
                            .status(OrderStatus.ORDERED)
                            .build();
                    orders.add(order);
                } catch (Exception e) {
                    log.warn("[Excel 파싱] {}행 오류 - {}", i + 1, e.getMessage());
                }
            }
        }

        orderRepository.saveAll(orders);
        log.info("[Excel 파싱] 발주 {}건 저장 완료", orders.size());
        return orders.size();
    }

    /**
     * 현재 발주 목록 → 엑셀 파일 생성
     */
    public Workbook exportOrders() {
        List<Order> orders = orderRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("발주목록");

        // 헤더 생성
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "품목명", "수량", "단가", "총액", "납품업체", "발주일", "납기일", "상태"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        // 데이터 생성
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(o.getId());
            row.createCell(1).setCellValue(o.getItemName());
            row.createCell(2).setCellValue(o.getQuantity());
            row.createCell(3).setCellValue(o.getUnitPrice());
            row.createCell(4).setCellValue((long) o.getQuantity() * o.getUnitPrice());
            row.createCell(5).setCellValue(o.getSupplier());
            row.createCell(6).setCellValue(o.getOrderDate().toString());
            row.createCell(7).setCellValue(o.getDueDate().toString());
            row.createCell(8).setCellValue(o.getStatus().name());
        }

        // 컬럼 너비 자동 조정
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("날짜 없음");
        return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}