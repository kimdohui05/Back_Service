# Database Schema (AI Context)

DB: `bank_service`

## Tables

### user
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
- uid: UUID (PK)
- id: login ID
- password: plain text (no encryption)

### account
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
- aid: UUID (PK)
- uid: FK to user
- acc_number: 12-digit account number
- balance: current balance (시간당 1% 자동 이자 적용)
- last_interest_update: 마지막 이자 적용 시각 (스케줄러가 업데이트)

### savings
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
- period: months
- mthly_deposit: monthly deposit amount

## Relationships
- User 1:N Account
- User 1:N Savings

## Implementation Status
- user, account: **Implemented** (with automatic hourly 1% interest)
- savings: **Not implemented**

## Interest System
- **Type**: Automatic hourly interest on all accounts
- **Rate**: 1% per hour (compound interest)
- **Method**: Spring @Scheduled runs every hour
- **Formula**: newBalance = floor(balance × 1.01^hoursElapsed)
- **Recovery**: Server downtime automatically recovered based on last_interest_update
