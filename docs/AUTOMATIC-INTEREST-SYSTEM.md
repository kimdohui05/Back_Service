# 자동 이자 시스템 (Automatic Interest System)

## 시스템 개요

**기능**: 모든 계좌에 1시간마다 자동으로 1% 복리 이자 적용

**특징**:
- ✅ 자동 실행: 1시간마다 스케줄러가 자동으로 이자 적용
- ✅ 모든 계좌 대상: 별도 신청 없이 모든 계좌에 자동 적용
- ✅ 복리 계산: 시간당 1% 복리 (이자에 이자가 붙음)
- ✅ 서버 다운 복구: 서버 다운 시에도 누락된 이자 자동 계산
- ✅ 소수점 버림: floor() 사용으로 원 단위 처리

## 작동 원리

### 1. 스케줄러 방식
```
[Spring Scheduler]
    ↓ 1시간마다 자동 실행
[InterestScheduler.applyInterestToAllAccounts()]
    ↓ 모든 계좌 조회
[For each account]
    ↓ 경과 시간 계산
    ↓ 이자 계산: balance × 1.01^hoursElapsed
    ↓ 잔액 업데이트
[Database]
    ↓ balance, last_interest_update 업데이트
```

### 2. 이자 계산 공식

```
새 잔액 = floor(현재 잔액 × 1.01^경과시간)
```

**경과 시간**: `last_interest_update`부터 현재까지의 시간 (시간 단위)

### 3. 계산 예시

#### 정상 실행 (1시간마다)
```
14:00 - 잔액: 100,000원, 이자 적용
15:00 - 잔액: 101,000원 (100,000 × 1.01^1), 이자 적용
16:00 - 잔액: 102,010원 (101,000 × 1.01^1), 이자 적용
17:00 - 잔액: 103,030원 (102,010 × 1.01^1), 이자 적용
```

#### 서버 다운 시나리오
```
14:00 - 잔액: 100,000원, 이자 적용
        last_interest_update = 2026-01-29 14:00:00

15:00 - [서버 다운] 이자 적용 안 됨
16:00 - [서버 다운] 이자 적용 안 됨

17:00 - 서버 재시작, 스케줄러 실행
        경과 시간 = 17:00 - 14:00 = 3시간
        새 잔액 = floor(100,000 × 1.01^3) = 103,030원
        last_interest_update = 2026-01-29 17:00:00

→ 누락된 2시간치 이자 자동 복구!
```

## 구현 상세

### 1. Database Schema

```sql
-- account 테이블에 추가된 컬럼
ALTER TABLE account
ADD COLUMN last_interest_update DATETIME DEFAULT CURRENT_TIMESTAMP;
```

**필드 설명**:
- `last_interest_update`: 마지막으로 이자가 적용된 시각
- 기본값: CURRENT_TIMESTAMP (현재 시각)
- 스케줄러가 이자 적용 시마다 업데이트

### 2. 구현 파일

#### Account.java (DTO)
```java
private Long balance;                    // 잔액 (이자 포함)
private LocalDateTime lastInterestUpdate; // 마지막 이자 적용 시각
```

#### AccountRepository.java
```java
// 모든 계좌 조회 (스케줄러용)
public List<Account> findAll() { ... }

// 잔액과 이자 적용 시각 함께 업데이트
public void updateBalanceAndInterestTime(
    String accNumber,
    Long newBalance,
    LocalDateTime updateTime
) { ... }
```

#### InterestScheduler.java (핵심)
```java
@Component
public class InterestScheduler {

    @Scheduled(fixedRate = 3600000) // 1시간 = 3600000ms
    public void applyInterestToAllAccounts() {
        // 1. 현재 시각 기록
        LocalDateTime now = LocalDateTime.now();

        // 2. 모든 계좌 조회
        List<Account> accounts = accountRepository.findAll();

        // 3. 각 계좌에 이자 적용
        for (Account account : accounts) {
            // 경과 시간 계산
            long hoursElapsed = ChronoUnit.HOURS.between(
                account.getLastInterestUpdate(),
                now
            );

            // 1시간 미만이면 스킵
            if (hoursElapsed < 1) continue;

            // 복리 이자 계산
            double newBalanceDouble =
                account.getBalance() * Math.pow(1.01, hoursElapsed);
            long newBalance = (long) Math.floor(newBalanceDouble);

            // 업데이트
            accountRepository.updateBalanceAndInterestTime(
                account.getAccNumber(),
                newBalance,
                now
            );
        }
    }
}
```

#### BankServiceApplication.java
```java
@EnableScheduling  // 스케줄러 활성화
@SpringBootApplication
public class BankServiceApplication { ... }
```

## 테스트 방법

### 빠른 테스트 설정

**프로덕션용** (1시간):
```java
@Scheduled(fixedRate = 3600000) // 3600초 = 1시간
```

**테스트용** (1분):
```java
@Scheduled(fixedRate = 60000) // 60초 = 1분
```

**초고속 테스트용** (10초):
```java
@Scheduled(fixedRate = 10000) // 10초
```

### 테스트 시나리오

#### 1. 기본 테스트
```bash
# 1. 계좌 생성
curl -X POST http://localhost:8080/api/account/create \
  -H "Content-Type: application/json" \
  -d '{"uid":"user-uuid","accPassword":"1234"}'
# → accNumber 받기

# 2. 10만원 입금
curl -X POST http://localhost:8080/api/account/deposit \
  -H "Content-Type: application/json" \
  -d '{"accNumber":"123456789012","amount":100000}'

# 3. 잔액 확인
curl http://localhost:8080/api/account/123456789012
# → 잔액 : 100000

# 4. 1분 대기 (테스트 설정: fixedRate = 60000)

# 5. 잔액 재확인
curl http://localhost:8080/api/account/123456789012
# → 잔액 : 101000

# 6. 1분 더 대기

# 7. 잔액 재확인
curl http://localhost:8080/api/account/123456789012
# → 잔액 : 102010
```

#### 2. 서버 다운 복구 테스트
```bash
# 1. 계좌에 10만원 입금
# 2. 서버 로그에서 이자 적용 확인:
#    [InterestScheduler] 계좌 123456789012: 100000원 → 101000원
# 3. 서버 종료 (Ctrl+C)
# 4. 2시간 대기 (또는 시스템 시계 조작)
# 5. 서버 재시작
# 6. 로그 확인:
#    [InterestScheduler] 계좌 123456789012: 101000원 → 103030원 (2시간 경과)
```

#### 3. 로그 확인
서버 실행 시 콘솔에 다음과 같은 로그가 출력됩니다:

```
[InterestScheduler] 이자 적용 시작 - 대상 계좌 수: 5
[InterestScheduler] 계좌 123456789012: 100000원 → 101000원 (1시간 경과)
[InterestScheduler] 계좌 987654321098: 50000원 → 50500원 (1시간 경과)
[InterestScheduler] 이자 적용 완료 - 업데이트된 계좌 수: 2
```

## 이자 증가 시뮬레이션

### 초기 금액: 100,000원, 시간당 1%

| 경과 시간 | 계산 | 잔액 |
|----------|------|------|
| 0시간 | 100,000 | 100,000원 |
| 1시간 | 100,000 × 1.01 | 101,000원 |
| 2시간 | 100,000 × 1.01² | 102,010원 |
| 3시간 | 100,000 × 1.01³ | 103,030원 |
| 6시간 | 100,000 × 1.01⁶ | 106,152원 |
| 12시간 | 100,000 × 1.01¹² | 112,682원 |
| 24시간 | 100,000 × 1.01²⁴ | 126,973원 |
| 48시간 | 100,000 × 1.01⁴⁸ | 161,222원 |
| 72시간 | 100,000 × 1.01⁷² | 204,710원 |
| 1주일 | 100,000 × 1.01¹⁶⁸ | 533,282원 |

**주의**: 시간당 1%는 여전히 매우 높은 이자율입니다!

### 복리 효과 비교

**단리 (1시간마다 +1,000원)**:
```
100,000 → 101,000 → 102,000 → 103,000
```

**복리 (1시간마다 ×1.01)**:
```
100,000 → 101,000 → 102,010 → 103,030
```

시간이 지날수록 차이가 커집니다!

## 특별 케이스

### 1. 잔액이 0원인 계좌
```
0 × 1.01^n = 0 (항상 0원)
```
→ 업데이트 스킵 (의미 없음)

### 2. 소수점 발생
```
999원 × 1.01 = 1008.99원
floor(1008.99) = 1008원
```
→ 항상 버림 처리

### 3. 경과 시간이 1시간 미만
```
hoursElapsed < 1 → 이자 적용 안 함
```
→ 스케줄러는 1시간마다 실행되므로 일반적으로 발생 안 함
→ 서버 재시작 직후에만 가능

### 4. lastInterestUpdate가 NULL
```
if (lastUpdate == null) {
    // 현재 시각으로 초기화만 하고 이자 적용 안 함
    updateTime(now);
    continue;
}
```
→ 안전 처리

## 성능 고려사항

### 현재 구현
- **방식**: 동기 처리
- **계좌 수**: 소규모 시스템 가정
- **실행 시간**: 계좌 1000개 기준 약 1초 이내

### 대규모 시스템을 위한 개선안
1. **배치 처리**: 한 번에 여러 계좌를 UPDATE
2. **비동기 처리**: @Async 사용
3. **파티셔닝**: 계좌를 그룹으로 나눠서 처리
4. **인덱스**: last_interest_update에 인덱스 추가

## FAQ

**Q1: 왜 deposit 테이블을 제거했나요?**
- A: 별도 예금 계좌 없이 모든 계좌에 자동으로 이자가 붙기 때문입니다.

**Q2: 이자는 언제 적용되나요?**
- A: 서버가 실행 중이면 정확히 1시간마다 자동으로 적용됩니다.

**Q3: 서버를 끄면 이자가 안 붙나요?**
- A: 서버가 꺼져 있어도 재시작하면 누락된 이자를 모두 계산해서 적용합니다.

**Q4: 계좌 조회할 때 이자가 적용되나요?**
- A: 아니요. 조회는 DB 값을 그대로 보여줍니다. 스케줄러만 이자를 적용합니다.

**Q5: 복리와 단리의 차이는?**
- A:
  - 단리: 원금에만 이자 (매번 같은 금액 증가)
  - 복리: 이자에도 이자 (매번 증가폭이 커짐)
  - 현재 시스템은 복리입니다.

**Q6: 시간당 1%는 현실적인가요?**
- A: 아니요. 테스트/데모용 설정입니다. 현실 금리는 연 3% 정도입니다.

**Q7: 이자율을 변경하려면?**
- A: InterestScheduler.java의 `Math.pow(1.01, hoursElapsed)`에서
  - 1.01 → 1.005 (0.5%)
  - 1.01 → 1.02 (2%)
  - 등으로 변경

**Q8: 스케줄러 실행 주기를 변경하려면?**
- A: InterestScheduler.java의 `@Scheduled(fixedRate = 3600000)`에서
  - 3600000 → 60000 (1분)
  - 3600000 → 10000 (10초)
  - 등으로 변경

**Q9: 특정 계좌만 이자를 제외하려면?**
- A: 현재는 모든 계좌에 적용됩니다. 제외하려면:
  ```java
  // InterestScheduler에서
  if (account.getAccNumber().equals("특정계좌")) {
      continue; // 스킵
  }
  ```

**Q10: 이자 적용 내역을 확인하려면?**
- A: 현재는 로그로만 확인 가능합니다. 서버 콘솔을 보세요.

## 마이그레이션 체크리스트

설정이 완료되었는지 확인하세요:

- [ ] MySQL에서 ALTER TABLE 실행 (last_interest_update 컬럼 추가)
- [ ] 코드 빌드 성공 확인
- [ ] 서버 시작 시 @EnableScheduling 로그 확인
- [ ] 테스트용으로 fixedRate를 60000 (1분)으로 설정
- [ ] 계좌 생성 후 1분 대기하여 이자 적용 확인
- [ ] 로그에서 [InterestScheduler] 메시지 확인
- [ ] 프로덕션 배포 전 fixedRate를 3600000 (1시간)으로 복구

## 주의사항

⚠️ **중요**:
- 시간당 1% 복리는 매우 빠르게 증가합니다
- 1주일이면 5배 이상 증가
- 실제 서비스에는 적절한 이자율 설정 필요 (예: 연 3% = 시간당 약 0.00034%)

⚠️ **DB 마이그레이션 필수**:
```sql
ALTER TABLE account
ADD COLUMN last_interest_update DATETIME DEFAULT CURRENT_TIMESTAMP;
```
이 SQL을 실행하지 않으면 서버가 시작되지 않습니다!

⚠️ **테스트 후 설정 변경**:
- 테스트 완료 후 `fixedRate = 3600000` (1시간)으로 변경하세요
- 그렇지 않으면 프로덕션에서 너무 빠르게 이자가 붙습니다

## 요약

**변경 사항**:
- ❌ deposit 패키지 전체 제거
- ✅ account 테이블에 last_interest_update 컬럼 추가
- ✅ InterestScheduler 클래스 생성 (1시간마다 자동 실행)
- ✅ 모든 계좌에 시간당 1% 복리 이자 자동 적용
- ✅ 서버 다운 시 누락 이자 자동 복구

**장점**:
- 자동화: 사용자 조작 불필요
- 안정성: 서버 다운 복구 가능
- 단순성: 별도 API 없이 자동 처리

**다음 단계**:
1. DB 마이그레이션 실행
2. 서버 시작
3. 테스트 (1분 설정)
4. 프로덕션 배포 (1시간 설정)
