# BankService Database Schema

## 개요
BankService 프로젝트의 MySQL 데이터베이스 스키마 문서입니다.

**Database Name**: `bank_service`

---

## 1. User (회원 정보)

사용자의 기본 정보를 저장하는 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| uid | VARCHAR(36) | PK, NN | 유저 고유번호 (UUID) |
| id | VARCHAR(15) | NN | 회원가입, 로그인 ID |
| password | VARCHAR(15) | NN | 회원가입, 로그인 Password |
| name | VARCHAR(20) | NN | 사용자 이름 |
| phone_number | VARCHAR(11) | NN | 전화번호 |
| email | VARCHAR(45) | NN | 이메일 |

### SQL
```sql
CREATE TABLE user (
    uid VARCHAR(36) PRIMARY KEY NOT NULL,
    id VARCHAR(15) NOT NULL,
    password VARCHAR(15) NOT NULL,
    name VARCHAR(20) NOT NULL,
    phone_number VARCHAR(11) NOT NULL,
    email VARCHAR(45) NOT NULL
);
```

---

## 2. Account (일반 계좌)

일반 입출금 계좌 정보를 저장하는 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| aid | VARCHAR(36) | PK, NN | 계좌 고유번호 (UUID) |
| uid | VARCHAR(36) | FK, NN | 유저 ID (외래키) |
| acc_number | VARCHAR(12) | NN | 계좌번호 |
| acc_password | VARCHAR(4) | NN | 계좌 비밀번호 |
| balance | DECIMAL(20, 2) | NN | 현재 잔액 |

### SQL
```sql
CREATE TABLE account (
    aid VARCHAR(36) PRIMARY KEY NOT NULL,
    uid VARCHAR(36) NOT NULL,
    acc_number VARCHAR(12) NOT NULL,
    acc_password VARCHAR(4) NOT NULL,
    balance DECIMAL(20, 2) NOT NULL,
    FOREIGN KEY (uid) REFERENCES user(uid)
);
```

---

## 3. Deposit (예금)

정기예금 계좌 정보를 저장하는 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| did | VARCHAR(36) | PK, NN | 예금 고유번호 (UUID) |
| uid | VARCHAR(36) | FK, NN | 유저 FK |
| acc_number | VARCHAR(12) | NN | 계좌번호 |
| acc_password | VARCHAR(4) | NN | 계좌 비밀번호 |
| rate | DECIMAL(4, 2) | NN | 이자율 (예: 3.50%) |
| start_date | DATE | NN | 시작일 |
| maturity_date | DATE | NN | 만기일 |
| status | ENUM | NN | 상태 (ACTIVE, MATURED, CLOSED) |
| balance | DECIMAL(20, 2) | NN | 잔액 |

### SQL
```sql
CREATE TABLE deposit (
    did VARCHAR(36) PRIMARY KEY NOT NULL,
    uid VARCHAR(36) NOT NULL,
    acc_number VARCHAR(12) NOT NULL,
    acc_password VARCHAR(4) NOT NULL,
    rate DECIMAL(4, 2) NOT NULL,
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    status ENUM('ACTIVE', 'MATURED', 'CLOSED') NOT NULL,
    balance DECIMAL(20, 2) NOT NULL,
    FOREIGN KEY (uid) REFERENCES user(uid)
);
```

---

## 4. Savings (적금)

정기적금 계좌 정보를 저장하는 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| sid | VARCHAR(36) | PK, NN | 적금 고유번호 (UUID) |
| uid | VARCHAR(36) | FK, NN | 유저 FK |
| acc_number | VARCHAR(12) | NN | 계좌번호 |
| acc_password | VARCHAR(4) | NN | 계좌 비밀번호 |
| rate | DECIMAL(4, 2) | NN | 이자율 |
| start_date | DATE | NN | 시작일 |
| status | ENUM | NN | 상태 (ACTIVE, MATURED, CLOSED) |
| balance | DECIMAL(20, 2) | NN | 현재 잔액 |
| period | INT | NN | 기간 (개월) |
| mthly_deposit | INT | NN | 월 납입액 |

### SQL
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
    mthly_deposit INT NOT NULL,
    FOREIGN KEY (uid) REFERENCES user(uid)
);
```

---

## 테이블 관계도

```
User (1) ─────< (N) Account
  │
  ├────────────< (N) Deposit
  │
  └────────────< (N) Savings
```

- 한 명의 사용자(User)는 여러 개의 계좌(Account, Deposit, Savings)를 가질 수 있음
- 모든 계좌 테이블은 `uid`를 통해 User 테이블과 연결됨

---

## 계좌 유형별 특징

| 구분 | Account | Deposit | Savings |
|------|---------|---------|---------|
| 유형 | 입출금 계좌 | 정기예금 | 정기적금 |
| 입출금 | 자유 | 만기 시 | 만기 시 |
| 납입 방식 | 자유 | 일시납 | 월 납입 |
| 이자 | 없음/낮음 | 고정 이자율 | 고정 이자율 |
| 만기일 | 없음 | 있음 | 계산됨 (start_date + period) |
| 월 납입액 | - | - | 고정 |

---

**작성일**: 2026-01-12
