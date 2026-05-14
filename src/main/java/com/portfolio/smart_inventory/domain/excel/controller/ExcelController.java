package com.portfolio.smart_inventory.domain.excel.controller;

import com.portfolio.smart_inventory.domain.excel.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    // 엑셀 업로드 → 발주 일괄 등록
    @PostMapping(value = "/import/orders", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importOrders(@RequestPart("file") MultipartFile file) throws IOException {
        int count = excelService.importOrders(file);
        return ResponseEntity.ok(count + "건 발주 데이터가 등록되었습니다.");
    }

    // 발주 목록 엑셀 다운로드
    @GetMapping("/export/orders")
    public ResponseEntity<byte[]> exportOrders() throws IOException {
        Workbook workbook = excelService.exportOrders();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        String fileName = URLEncoder.encode("발주목록.xlsx", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }
}