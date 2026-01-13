# BankService - Quick Reference (AI Context)

## Tech Stack
- Spring Boot 3.5.7, Gradle, MySQL
- Spring Data JDBC, Spring Security, Validation

## Project Structure
```
src/main/java/com/example/bankservice/
├── account/
│   ├── controller/AccountController.java
│   ├── service/AccountService.java
│   ├── repository/AccountRepository.java
│   └── dto/ (Account, AccountCreateRequest, DepositRequest, WithdrawRequest, TransferRequest)
├── user/
│   ├── controller/UserController.java
│   ├── service/UserService.java
│   ├── repository/UserRepository.java
│   └── dto/ (User, Login)
├── config/SecurityConfig.java
└── BankServiceApplication.java
```

## API Endpoints

### User API - `/api/user`
- POST `/register` - 회원가입
- POST `/login` - 로그인
- GET `/{id}` - ID로 사용자 조회

### Account API - `/api/account`
- POST `/create` - 계좌 개설
- GET `/{accNumber}` - 계좌 조회
- GET `/my/{uid}` - 내 계좌 목록
- POST `/deposit` - 입금
- POST `/withdraw` - 출금
- POST `/transfer` - 송금

## Architecture
Controller → Service → Repository → MySQL

## Implementation Status
**Completed:**
- User: register, login, getUserById
- Account: create, getInfo, getMyAccounts, deposit, withdraw, transfer
- SecurityConfig: all requests permitAll

**Not Implemented:**
- Deposit (예금), Savings (적금)
- JWT authentication
- Exception handling
- Logging
- Tests

## Database
- DB Name: `bank_service`
- Tables: user, account, deposit, savings
- See: `database-schema.md`

## Key Points
- 3-layer architecture
- Constructor injection
- Spring Security CSRF disabled, all requests permitted
- No password encryption (plain text)
