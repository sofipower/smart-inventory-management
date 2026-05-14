# 🏭 발주·재고 통합 관리 시스템

> 현업 구매 담당자가 직접 경험한 문제를 코드로 해결한 프로젝트

[![Java](https://img.shields.io/badge/Java-21-007396?style=flat&logo=java)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat&logo=springboot)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql)]()
[![React](https://img.shields.io/badge/React-18-61DAFB?style=flat&logo=react)]()
[![Spring AI](https://img.shields.io/badge/Spring_AI-연동-6DB33F?style=flat)]()
[![Swagger](https://img.shields.io/badge/Swagger-API문서-85EA2D?style=flat&logo=swagger)]()

🔗 **배포 URL**: [https://your-app-url.com](https://your-app-url.com)
📅 **개발 기간**: 2026.05 ~ 2026.06 (2주)


## 📌 프로젝트 소개

산업공구 유통 기업에서 2년간 발주·납기·재고·미납 관리를
ERP로 직접 운영하며 시스템의 한계를 느꼈습니다.
이 경험을 바탕으로 **현장 흐름에 맞는 재고 관리 REST API**를 구현하였습니다.


## ✨ 주요 기능

| 기능 | 설명 |
|------|------|
| 📦 발주 관리 | 발주 등록·수정·삭제 및 납기일 추적 |
| 📊 재고 관리 | 안전재고 기준 설정 및 부족 감지 |
| ⚠️ 미납 자동 알림 | Spring Scheduler로 매일 오전 9시 자동 체크 |
| 📁 Excel 파싱 | SheetJS 기반 멀티시트 Excel 일괄 업로드 |
| 🤖 AI 발주 추천 | Spring AI로 재고 부족 시 발주 메시지 자동 생성 |


## 🖼 화면

### 재고 현황 대시보드
![대시보드 스크린샷](./docs/images/dashboard.png)

### Excel 파싱 결과
![Excel 파싱](./docs/images/excel.png)


## 🏗 아키텍처

```
Client (React)
    ↓ HTTP
Spring Boot (REST API)
    ↓ JPA
MySQL 8.0
    + Spring AI (OpenAI API)
    + Spring Scheduler (미납 자동 체크)
```


## 🔧 트러블슈팅

### 문제: Excel 금액 셀 쉼표 포함 시 숫자 파싱 오류
- **원인**: "1,200,000" 문자열을 직접 숫자 변환 시 NumberFormatException 발생
- **해결**: `String.replace(",", "")` 전처리 후 `Double.parseDouble()` 적용
- **배운 점**: 외부 데이터 입력 시 타입 안전성 검증 단계 필수

### 문제: 멀티시트 Excel 처리 시 일부 시트 누락
- **원인**: 특정 시트명에 공백 포함 시 매핑 실패
- **해결**: `sheet.getSheetName().trim()` 처리로 해결


## 🛠 기술 스택

- **Backend**: Java 21, Spring Boot 3.x, Spring Data JPA
- **Database**: MySQL 8.0
- **AI**: Spring AI (OpenAI API 연동)
- **Scheduler**: Spring Scheduler
- **Docs**: Swagger UI


## ▶ 실행 방법

```bash
# 1. 레포 클론
git clone https://github.com/sofipower/smart-inventory-management

# 2. application.yml 설정
spring.datasource.url=jdbc:mysql://localhost:3306/[db-name]

# 3. 실행
./gradlew bootRun
```

API 문서: `http://localhost:8080/swagger-ui.html`
