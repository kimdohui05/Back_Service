# 이자 시스템 마이그레이션 가이드

## 변경 개요
- **기존**: 별도 예금 계좌를 만들고 예금 생성/해지 방식
- **신규**: 모든 계좌에 1시간마다 자동으로 1% 이자 추가

## 1. 데이터베이스 마이그레이션

### Step 1: deposit 테이블 삭제 (선택사항)
```sql
-- 기존 deposit 테이블이 있다면 삭제
DROP TABLE IF EXISTS deposit;
```

### Step 2: account 테이블에 컬럼 추가
```sql
-- 이자 적용을 위한 필드 추가
ALTER TABLE account
ADD COLUMN last_interest_update DATETIME DEFAULT CURRENT_TIMESTAMP;
```

**필드 설명**:
- `last_interest_update`: 마지막으로 이자가 적용된 시각
- DEFAULT CURRENT_TIMESTAMP: 기존 계좌는 현재 시각으로 초기화
- 새 계좌 생성 시 자동으로 현재 시각 설정

### 업데이트된 account 테이블 전체 구조
```sql
CREATE TABLE account (
    aid VARCHAR(36) PRIMARY KEY NOT NULL,
    uid VARCHAR(36) NOT NULL,
    acc_number VARCHAR(12) NOT NULL,
    acc_password VARCHAR(4) NOT NULL,
    balance DECIMAL(20, 2) NOT NULL,
    last_interest_update DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uid) REFERENCES user(uid)
);
```

## 2. 이자 시스템 작동 원리

### 자동 스케줄러 방식
- Spring @Scheduled를 사용하여 1시간마다 자동 실행
- 모든 계좌를 조회하여 이자 적용

### 이자 계산 공식
```
새 잔액 = floor(현재 잔액 × 1.01^경과시간)
```

### 예시
```
계좌 잔액: 100,000원
last_interest_update: 2026-01-29 14:00:00

스케줄러 실행 (15:00):
- 경과 시간: 1시간
- 새 잔액: floor(100,000 × 1.01^1) = 101,000원
- last_interest_update: 2026-01-29 15:00:00으로 업데이트

스케줄러 실행 (16:00):
- 경과 시간: 1시간
- 새 잔액: floor(101,000 × 1.01^1) = 102,010원
- last_interest_update: 2026-01-29 16:00:00으로 업데이트
```

### 서버 다운 시나리오
```
14:00 - 이자 적용 (100,000원)
15:00 - 서버 다운 (이자 적용 못 함)
16:00 - 서버 다운 (이자 적용 못 함)
17:00 - 서버 재시작 및 스케줄러 실행
  → 경과 시간: 3시간 (14:00 ~ 17:00)
  → 새 잔액: floor(100,000 × 1.01^3) = 103,030원
  → 누락된 이자 자동 복구
```

## 3. 구현 파일

### 생성할 파일
- `src/main/java/com/example/bankservice/scheduler/InterestScheduler.java`

### 수정할 파일
- `src/main/java/com/example/bankservice/account/dto/Account.java`
- `src/main/java/com/example/bankservice/account/repository/AccountRepository.java`
- `src/main/java/com/example/bankservice/account/service/AccountService.java`

### 삭제된 파일
- `src/main/java/com/example/bankservice/deposit/` (전체 패키지)

## 4. 스케줄러 설정

### application.properties 또는 application.yml
```properties
# 스케줄러 활성화
spring.task.scheduling.pool.size=1
```

### Main 클래스에 @EnableScheduling 추가
```java
@EnableScheduling  // 추가 필요
@SpringBootApplication
public class BankServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankServiceApplication.class, args);
    }
}
```

## 5. 테스트 방법

### 빠른 테스트를 위한 설정 변경
스케줄러를 1시간마다 실행하면 테스트하기 어려우므로:

**테스트용**: 1분마다 실행
```java
@Scheduled(fixedRate = 60000) // 60초 = 1분
```

**프로덕션용**: 1시간마다 실행
```java
@Scheduled(fixedRate = 3600000) // 3600초 = 1시간
```

### 테스트 시나리오
```
1. 계좌 생성 후 10만원 입금
2. 1분 대기 (테스트용 설정)
3. 계좌 조회 → 잔액 101,000원 확인
4. 1분 더 대기
5. 계좌 조회 → 잔액 102,010원 확인
```

## 6. 주의사항

### 이자 지급 대상
- 현재 구현: **모든 계좌**에 이자 지급
- 잔액이 0원인 계좌도 포함 (0 × 1.01 = 0이므로 실제로는 변화 없음)

### 성능 고려사항
- 계좌가 많을 경우 스케줄러 실행 시간이 길어질 수 있음
- 필요시 배치 처리 또는 비동기 처리 고려

### 복리 이자 효과
```
초기 금액: 100,000원
시간당 1% 복리

1시간 후: 101,000원
2시간 후: 102,010원
3시간 후: 103,030원
24시간 후: 126,973원
1주일 후: 약 320,000원
```

시간당 1%는 여전히 매우 높은 이자율입니다. (연환산 시 천문학적 수치)
