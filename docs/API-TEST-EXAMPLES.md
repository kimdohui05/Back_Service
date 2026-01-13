# Account API 테스트 가이드

## 사전 준비
1. MySQL 서버 실행
2. `bank_service` 데이터베이스 생성
3. Spring Boot 애플리케이션 실행: `gradlew.bat bootRun`
4. 서버 주소: `http://localhost:8080`

---

## API 테스트 예제

### 1. 회원가입 (User API)
```bash
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "id": "hong",
  "password": "1234",
  "name": "홍길동",
  "phoneNumber": "01012345678",
  "email": "hong@example.com"
}
```

**curl 명령어:**
```bash
curl -X POST http://localhost:8080/api/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"hong\",\"password\":\"1234\",\"name\":\"홍길동\",\"phoneNumber\":\"01012345678\",\"email\":\"hong@example.com\"}"
```

---

### 2. 로그인 (User API)
```bash
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "id": "hong",
  "password": "1234"
}
```

**curl 명령어:**
```bash
curl -X POST http://localhost:8080/api/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"hong\",\"password\":\"1234\"}"
```

---

### 3. 계좌 개설
```bash
POST http://localhost:8080/api/accounts/create
Content-Type: application/json

{
  "uid": "사용자의_uid값",
  "accPassword": "1234"
}
```

**curl 명령어:**
```bash
curl -X POST http://localhost:8080/api/accounts/create ^
  -H "Content-Type: application/json" ^
  -d "{\"uid\":\"사용자의_uid값\",\"accPassword\":\"1234\"}"
```

**응답 예:**
```json
{
  "aid": "550e8400-...",
  "uid": "사용자의_uid값",
  "accNumber": "123456789012",
  "accPassword": "1234",
  "balance": 0.00
}
```

---

### 4. 계좌 조회 (잔액 조회)
```bash
GET http://localhost:8080/api/accounts/123456789012
```

**curl 명령어:**
```bash
curl http://localhost:8080/api/accounts/123456789012
```

**응답 예:**
```json
{
  "aid": "550e8400-...",
  "uid": "사용자의_uid값",
  "accNumber": "123456789012",
  "accPassword": "1234",
  "balance": 50000.00
}
```

---

### 5. 내 계좌 목록 조회
```bash
GET http://localhost:8080/api/accounts/my/사용자의_uid값
```

**curl 명령어:**
```bash
curl http://localhost:8080/api/accounts/my/사용자의_uid값
```

**응답 예:**
```json
[
  {
    "aid": "550e8400-...",
    "uid": "사용자의_uid값",
    "accNumber": "123456789012",
    "accPassword": "1234",
    "balance": 50000.00
  },
  {
    "aid": "660e9500-...",
    "uid": "사용자의_uid값",
    "accNumber": "987654321098",
    "accPassword": "5678",
    "balance": 100000.00
  }
]
```

---

### 6. 입금
```bash
POST http://localhost:8080/api/accounts/deposit
Content-Type: application/json

{
  "accNumber": "123456789012",
  "amount": 50000
}
```

**curl 명령어:**
```bash
curl -X POST http://localhost:8080/api/accounts/deposit ^
  -H "Content-Type: application/json" ^
  -d "{\"accNumber\":\"123456789012\",\"amount\":50000}"
```

**응답:** `"입금 성공"`

---

### 7. 출금
```bash
POST http://localhost:8080/api/accounts/withdraw
Content-Type: application/json

{
  "accNumber": "123456789012",
  "accPassword": "1234",
  "amount": 10000
}
```

**curl 명령어:**
```bash
curl -X POST http://localhost:8080/api/accounts/withdraw ^
  -H "Content-Type: application/json" ^
  -d "{\"accNumber\":\"123456789012\",\"accPassword\":\"1234\",\"amount\":10000}"
```

**응답:**
- 성공: `"출금 성공"`
- 실패: `"계좌 비밀번호가 틀렸습니다"` 또는 `"잔액이 부족합니다"`

---

### 8. 송금
```bash
POST http://localhost:8080/api/accounts/transfer
Content-Type: application/json

{
  "fromAccNumber": "123456789012",
  "toAccNumber": "987654321098",
  "accPassword": "1234",
  "amount": 20000
}
```

**curl 명령어:**
```bash
curl -X POST http://localhost:8080/api/accounts/transfer ^
  -H "Content-Type: application/json" ^
  -d "{\"fromAccNumber\":\"123456789012\",\"toAccNumber\":\"987654321098\",\"accPassword\":\"1234\",\"amount\":20000}"
```

**응답:**
- 성공: `"송금 성공"`
- 실패: `"계좌 비밀번호가 틀렸습니다"` 또는 `"잔액이 부족합니다"`

---

## 테스트 시나리오 예제

### 완전한 테스트 흐름:

1. **회원가입**: 홍길동 회원가입
2. **로그인**: 홍길동 로그인 (uid 확인)
3. **계좌 개설**: 홍길동의 계좌 개설 (계좌번호 확인)
4. **입금**: 계좌에 50,000원 입금
5. **잔액 조회**: 잔액 확인 (50,000원)
6. **출금**: 10,000원 출금
7. **잔액 조회**: 잔액 확인 (40,000원)
8. **두 번째 계좌 개설**: 김철수의 계좌 개설
9. **송금**: 홍길동 → 김철수에게 20,000원 송금
10. **잔액 조회**: 홍길동 잔액 (20,000원), 김철수 잔액 (20,000원)

---

## Postman으로 테스트하기

1. Postman 실행
2. New Request 생성
3. HTTP Method 선택 (GET, POST 등)
4. URL 입력: `http://localhost:8080/api/accounts/create`
5. Body 탭 → raw → JSON 선택
6. JSON 데이터 입력
7. Send 버튼 클릭
8. 응답 확인

---

## IntelliJ IDEA HTTP Client로 테스트하기

1. IntelliJ에서 `.http` 파일 생성
2. 위 예제 복사 (curl 빼고)
3. 요청 옆의 실행 버튼 클릭
4. 응답 확인

---

## 주의사항

1. **uid 값**: 회원가입 후 데이터베이스에서 uid를 확인하거나, User 조회 API를 추가로 만들어야 함
2. **계좌번호**: 계좌 개설 시 응답으로 받은 계좌번호를 사용
3. **MySQL 연결**: application.properties의 DB 설정 확인
4. **포트 확인**: 기본 포트는 8080, 변경했다면 해당 포트 사용
