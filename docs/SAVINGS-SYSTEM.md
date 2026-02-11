# 적금 시스템 (Daily Savings System)

## 시스템 개요

**기능**: 일일 납입 적금 계좌 시스템

**특징**:
- ✅ 일일 납입: 하루에 한 번만 입금 가능
- ✅ 고정 금액: 10,000원, 30,000원, 50,000원, 100,000원 중 선택
- ✅ 자동 이자: 매일 자정에 11% 이자 자동 적용
- ✅ 입금한 날만 이자 적용: 입금하지 않은 날은 이자가 붙지 않음
- ✅ 계좌번호 구분: 9로 시작 (일반 계좌는 0으로 시작)

## 작동 원리

### 1. 일일 납입 시스템

```
[사용자]
  ↓ 적금 개설 (일일 납입액 선택: 10000/30000/50000/100000)
[적금 계좌 생성]
  ↓ 계좌번호 9로 시작, 초기 잔액 0원
[매일 입금]
  ↓ 하루에 한 번만 입금 가능
  ↓ 입금액 = 설정한 일일 납입액
[자정 스케줄러]
  ↓ 어제 입금한 계좌에만 11% 이자 적용
[잔액 증가]
```

### 2. 이자 계산 공식

```
이자 = floor(현재 잔액 × 0.11)
새 잔액 = 현재 잔액 + 이자
```

### 3. 계산 예시

#### 일일 납입액 10,000원, 10일간 매일 입금

```
Day 1 (입금):
  - 입금 전: 0원
  - 입금 후: 10,000원
  - 자정 이자: 10,000 × 0.11 = 1,100원
  - 자정 후 잔액: 11,100원

Day 2 (입금):
  - 입금 전: 11,100원
  - 입금 후: 21,100원
  - 자정 이자: 21,100 × 0.11 = 2,321원
  - 자정 후 잔액: 23,421원

Day 3 (입금):
  - 입금 전: 23,421원
  - 입금 후: 33,421원
  - 자정 이자: 33,421 × 0.11 = 3,676원
  - 자정 후 잔액: 37,097원

Day 4 (입금):
  - 입금 전: 37,097원
  - 입금 후: 47,097원
  - 자정 이자: 47,097 × 0.11 = 5,180원
  - 자정 후 잔액: 52,277원

Day 5 (입금):
  - 입금 전: 52,277원
  - 입금 후: 62,277원
  - 자정 이자: 62,277 × 0.11 = 6,850원
  - 자정 후 잔액: 69,127원

Day 6 (입금):
  - 입금 전: 69,127원
  - 입금 후: 79,127원
  - 자정 이자: 79,127 × 0.11 = 8,703원
  - 자정 후 잔액: 87,830원

Day 7 (입금):
  - 입금 전: 87,830원
  - 입금 후: 97,830원
  - 자정 이자: 97,830 × 0.11 = 10,761원
  - 자정 후 잔액: 108,591원

Day 8 (입금):
  - 입금 전: 108,591원
  - 입금 후: 118,591원
  - 자정 이자: 118,591 × 0.11 = 13,045원
  - 자정 후 잔액: 131,636원

Day 9 (입금):
  - 입금 전: 131,636원
  - 입금 후: 141,636원
  - 자정 이자: 141,636 × 0.11 = 15,579원
  - 자정 후 잔액: 157,215원

Day 10 (입금):
  - 입금 전: 157,215원
  - 입금 후: 167,215원
  - 자정 이자: 167,215 × 0.11 = 18,393원
  - 자정 후 잔액: 185,608원

→ 10일간 100,000원 입금 → 최종 잔액 185,608원 (이자 85,608원)
```

#### 입금하지 않은 날 예시

```
Day 1 (입금): 10,000원 입금 → 자정 이자 1,100원 → 11,100원
Day 2 (입금 안 함): 잔액 변동 없음 → 11,100원 유지
Day 3 (입금): 10,000원 입금 (21,100원) → 자정 이자 2,321원 → 23,421원
```

**중요**: 입금한 날만 자정에 이자가 붙습니다!

## 데이터베이스 스키마

### 필수 변경 사항

```sql
-- savings 테이블에 last_deposit_date 컬럼 추가
ALTER TABLE savings
ADD COLUMN last_deposit_date DATE;
```

### savings 테이블 전체 구조

```sql
CREATE TABLE savings (
    sid VARCHAR(36) PRIMARY KEY NOT NULL,
    uid VARCHAR(36) NOT NULL,
    acc_number VARCHAR(12) NOT NULL,
    acc_password VARCHAR(4) NOT NULL,
    rate DECIMAL(4, 2) NOT NULL,
    start_date DATE NOT NULL,
    status ENUM('ACTIVE', 'MATURED', 'CLOSED') NOT NULL,
    balance DECIMAL(20, 2) NOT NULL,
    period INT NOT NULL,
    mthly_deposit INT NOT NULL,  -- 일일 납입액으로 사용
    last_deposit_date DATE,      -- 마지막 입금 날짜 (NEW!)
    FOREIGN KEY (uid) REFERENCES user(uid)
);
```

**컬럼 설명**:
- `mthly_deposit`: 컬럼명은 월 납입액이지만 일일 납입액으로 사용
- `last_deposit_date`: 마지막 입금 날짜 (하루에 한 번만 입금 체크용)
- `period`: 적금 기간 (일 수)
- `rate`: 이자율 (11% 고정)

## API 엔드포인트

### 적금 계좌 API - `/api/savings`

#### 1. 적금 계좌 개설
```http
POST /api/savings/create
Content-Type: application/json

{
  "uid": "user-uuid",
  "accPassword": "1234",
  "rate": 11.0,
  "period": 30,
  "dailyDeposit": 10000  // 10000, 30000, 50000, 100000 중 선택
}
```

**응답**:
```
적금 계좌 개설에 성공했습니다
일일 납입액: 10000원
이자율: 11% (매일 자정 적용)
현재 보유하고 있는 적금 계좌번호 : 987654321098
```

#### 2. 적금 입금
```http
POST /api/savings/deposit
Content-Type: application/json

{
  "accNumber": "987654321098",
  "amount": 10000  // 설정한 일일 납입액과 일치해야 함
}
```

**응답**:
```
적금 입금 성공
입금액: 10000원
현재 잔액: 10000원
다음 입금 가능 날짜: 2026-02-01
```

**에러**:
- 일일 납입액과 다른 금액: "입금액은 설정된 일일 납입액(10000원)과 일치해야 합니다"
- 오늘 이미 입금: "오늘은 이미 입금하셨습니다. 내일 다시 입금해주세요"

#### 3. 적금 계좌 조회
```http
GET /api/savings/{accNumber}
```

**응답**:
```json
{
  "sid": "uuid",
  "uid": "user-uuid",
  "accNumber": "987654321098",
  "accPassword": "1234",
  "rate": 11.0,
  "startDate": "2026-01-31",
  "status": "ACTIVE",
  "balance": 10000,
  "period": 30,
  "dailyDeposit": 10000,
  "lastDepositDate": "2026-01-31"
}
```

#### 4. 내 적금 계좌 목록 조회
```http
GET /api/savings/my/{uid}
```

#### 5. 적금 해지
```http
POST /api/savings/close
Content-Type: application/json

{
  "accNumber": "987654321098",
  "accPassword": "1234"
}
```

**응답**:
```
적금 해지 성공
해지 금액: 185608원
```

## 스케줄러 동작

### SavingsInterestScheduler

**실행 시각**: 매일 자정 00:00:00

**동작**:
1. 어제 날짜 계산 (`LocalDate.now().minusDays(1)`)
2. 어제 입금한 ACTIVE 상태의 적금 계좌들 조회
3. 각 계좌에 11% 이자 적용
4. 잔액 업데이트

**로그**:
```
[SavingsInterestScheduler] 적금 이자 적용 시작 - 대상 계좌 수: 5
[SavingsInterestScheduler] 기준 날짜(어제): 2026-01-30
[SavingsInterestScheduler] 계좌 987654321098: 10000원 → 11100원 (이자: 1100원, +11%)
[SavingsInterestScheduler] 계좌 987654321099: 50000원 → 55500원 (이자: 5500원, +11%)
[SavingsInterestScheduler] 적금 이자 적용 완료 - 업데이트된 계좌 수: 5
```

## 테스트 방법

### 1. 기본 테스트

```bash
# 1. 적금 계좌 개설
curl -X POST http://localhost:8080/api/savings/create \
  -H "Content-Type: application/json" \
  -d '{
    "uid":"user-uuid",
    "accPassword":"1234",
    "rate":11.0,
    "period":30,
    "dailyDeposit":10000
  }'

# 2. 적금 입금 (10,000원)
curl -X POST http://localhost:8080/api/savings/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "accNumber":"987654321098",
    "amount":10000
  }'

# 3. 계좌 조회
curl http://localhost:8080/api/savings/987654321098

# 4. 자정 대기 (또는 스케줄러 테스트 모드 활성화)

# 5. 계좌 조회 (이자 적용 확인)
curl http://localhost:8080/api/savings/987654321098
# → 잔액: 11,100원 (1,100원 이자 추가)
```

### 2. 스케줄러 빠른 테스트

**SavingsInterestScheduler.java 수정**:

1. 프로덕션 스케줄러 주석 처리:
```java
// @Scheduled(cron = "0 0 0 * * *")
// public void applyInterestToSavings() { ... }
```

2. 테스트 스케줄러 활성화 (주석 해제):
```java
@Scheduled(fixedRate = 60000) // 1분마다
public void applyInterestToSavingsTest() { ... }
```

3. 테스트 완료 후 원래대로 복구

### 3. 하루에 한 번만 입금 가능 테스트

```bash
# 1. 첫 번째 입금 (성공)
curl -X POST http://localhost:8080/api/savings/deposit \
  -H "Content-Type: application/json" \
  -d '{"accNumber":"987654321098","amount":10000}'
# → "적금 입금 성공"

# 2. 같은 날 두 번째 입금 시도 (실패)
curl -X POST http://localhost:8080/api/savings/deposit \
  -H "Content-Type: application/json" \
  -d '{"accNumber":"987654321098","amount":10000}'
# → "오늘은 이미 입금하셨습니다. 내일 다시 입금해주세요"
```

### 4. 일일 납입액 검증 테스트

```bash
# 설정된 일일 납입액이 10,000원인 경우

# 성공 (10,000원)
curl -X POST http://localhost:8080/api/savings/deposit \
  -H "Content-Type: application/json" \
  -d '{"accNumber":"987654321098","amount":10000}'
# → "적금 입금 성공"

# 실패 (20,000원)
curl -X POST http://localhost:8080/api/savings/deposit \
  -H "Content-Type: application/json" \
  -d '{"accNumber":"987654321098","amount":20000}'
# → "입금액은 설정된 일일 납입액(10000원)과 일치해야 합니다"
```

## 적금 수익 시뮬레이션

### 일일 납입액별 30일 수익

| 일일 납입액 | 총 납입액 | 30일 후 예상 잔액 | 총 이자 |
|----------|---------|---------------|--------|
| 10,000원 | 300,000원 | 약 700,000원 | 약 400,000원 |
| 30,000원 | 900,000원 | 약 2,100,000원 | 약 1,200,000원 |
| 50,000원 | 1,500,000원 | 약 3,500,000원 | 약 2,000,000원 |
| 100,000원 | 3,000,000원 | 약 7,000,000원 | 약 4,000,000원 |

**참고**: 11% 일일 이자는 매우 높은 이자율입니다. 실제 금융 상품은 연 3-5% 정도입니다.

## 주의사항

⚠️ **데이터베이스 마이그레이션 필수**:
```sql
ALTER TABLE savings
ADD COLUMN last_deposit_date DATE;
```
이 SQL을 실행하지 않으면 서버가 시작되지 않습니다!

⚠️ **일일 이자 11%**:
- 매우 빠르게 증가합니다
- 30일이면 약 2-3배 증가
- 테스트/데모용 설정입니다

⚠️ **입금 규칙**:
- 하루에 한 번만 입금 가능
- 설정한 일일 납입액만 입금 가능
- 입금한 날만 자정에 이자 적용

⚠️ **계좌번호 구분**:
- 일반 계좌: 0으로 시작 (예: 012345678901)
- 적금 계좌: 9로 시작 (예: 987654321098)

## 구현 파일 목록

### DTO
- `savings/dto/Savings.java`
- `savings/dto/SavingsCreateRequest.java`
- `savings/dto/SavingsDepositRequest.java`
- `savings/dto/CloseSavingsRequest.java`

### Repository
- `savings/repository/SavingsRepository.java`

### Service
- `savings/service/SavingsService.java`

### Controller
- `savings/controller/SavingsController.java`

### Scheduler
- `savings/scheduler/SavingsInterestScheduler.java`

## FAQ

**Q1: 왜 입금하지 않은 날은 이자가 안 붙나요?**
- A: 일일 납입 적금 시스템이기 때문에 입금한 날만 이자를 적용합니다.

**Q2: 자정이 지나면 즉시 이자가 붙나요?**
- A: 네, 스케줄러가 자정(00:00:00)에 실행되어 어제 입금한 계좌에 이자를 적용합니다.

**Q3: 일일 납입액을 바꿀 수 있나요?**
- A: 아니요. 개설 시 선택한 일일 납입액은 변경할 수 없습니다.

**Q4: 왜 일일 납입액이 4가지로 고정되어 있나요?**
- A: 시스템 정책으로 10,000원, 30,000원, 50,000원, 100,000원만 선택 가능합니다.

**Q5: 적금을 중도 해지하면 이자는 어떻게 되나요?**
- A: 현재 잔액 그대로 해지됩니다 (페널티 없음).

**Q6: 이자율을 변경하려면?**
- A: `SavingsInterestScheduler.java`에서 `0.11`을 다른 값으로 변경:
  - 5%: `0.05`
  - 10%: `0.10`
  - 20%: `0.20`

**Q7: 적금 기간이 지나면 자동으로 만기되나요?**
- A: 현재는 자동 만기 처리가 구현되어 있지 않습니다. 추가 구현 필요.

**Q8: 일반 계좌와 적금 계좌를 구분하는 방법은?**
- A: 계좌번호 첫 자리로 구분:
  - 0으로 시작: 일반 계좌
  - 9로 시작: 적금 계좌

## 요약

**변경 사항**:
- ✅ 적금 계좌 시스템 신규 생성
- ✅ 계좌번호 9로 시작 (일반 계좌는 0으로 시작)
- ✅ 일일 납입액: 10000, 30000, 50000, 100000 중 선택
- ✅ 하루에 한 번만 입금 가능
- ✅ 매일 자정 11% 이자 자동 적용
- ✅ 입금한 날만 이자 적용

**다음 단계**:
1. DB 마이그레이션 실행 (`ALTER TABLE savings ADD COLUMN last_deposit_date DATE`)
2. 서버 시작
3. 적금 계좌 개설 테스트
4. 입금 테스트
5. 스케줄러 테스트 (자정 또는 테스트 모드)
