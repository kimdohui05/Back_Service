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

## Test Flow
1. Register user → get uid from DB or user query
2. Login → verify
3. Create account → get accNumber
4. Deposit → check balance
5. Withdraw → check balance
6. Create 2nd account
7. Transfer → check both balances
