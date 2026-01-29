# API Quick Test Guide (AI Context)

Server: `http://localhost:8080`

## User API

### Register
```bash
curl -X POST http://localhost:8080/api/user/register -H "Content-Type: application/json" -d "{\"id\":\"hong\",\"password\":\"1234\",\"name\":\"홍길동\",\"phoneNumber\":\"01012345678\",\"email\":\"hong@example.com\"}"
```

### Login
```bash
curl -X POST http://localhost:8080/api/user/login -H "Content-Type: application/json" -d "{\"id\":\"hong\",\"password\":\"1234\"}"
```

### Get User by ID
```bash
curl http://localhost:8080/api/user/hong
```

## Account API

### Create Account
```bash
curl -X POST http://localhost:8080/api/account/create -H "Content-Type: application/json" -d "{\"uid\":\"USER_UID\",\"accPassword\":\"1234\"}"
```
Response: Account object with `aid`, `accNumber`, `balance`

### Get Account
```bash
curl http://localhost:8080/api/account/123456789012
```

### Get My Accounts
```bash
curl http://localhost:8080/api/account/my/USER_UID
```

### Deposit
```bash
curl -X POST http://localhost:8080/api/account/deposit -H "Content-Type: application/json" -d "{\"accNumber\":\"123456789012\",\"amount\":50000}"
```

### Withdraw
```bash
curl -X POST http://localhost:8080/api/account/withdraw -H "Content-Type: application/json" -d "{\"accNumber\":\"123456789012\",\"accPassword\":\"1234\",\"amount\":10000}"
```

### Transfer
```bash
curl -X POST http://localhost:8080/api/account/transfer -H "Content-Type: application/json" -d "{\"fromAccNumber\":\"123456789012\",\"toAccNumber\":\"987654321098\",\"accPassword\":\"1234\",\"amount\":20000}"
```

## Interest System

### Automatic Interest Application
- 1시간마다 자동으로 모든 계좌에 1% 이자 적용
- Spring @Scheduled 사용
- 복리 계산: balance × 1.01^경과시간

### Testing Interest
```bash
# 1. 계좌에 돈 입금
curl -X POST http://localhost:8080/api/account/deposit -H "Content-Type: application/json" -d "{\"accNumber\":\"123456789012\",\"amount\":100000}"

# 2. 잔액 확인
curl http://localhost:8080/api/account/123456789012
# Expected: 잔액 : 100000

# 3. 1시간 대기 (또는 스케줄러를 1분으로 설정하고 1분 대기)

# 4. 잔액 재확인
curl http://localhost:8080/api/account/123456789012
# Expected: 잔액 : 101000 (1% 이자 적용)
```

## Test Flow
1. Register user → get uid from DB or user query
2. Login → verify
3. Create account → get accNumber
4. Deposit money → check balance
5. Wait 1 hour (or 1 minute with test config) → check balance (interest applied)
6. Withdraw → check balance
7. Create 2nd account
8. Transfer → check both balances
