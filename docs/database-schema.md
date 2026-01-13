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
    FOREIGN KEY (uid) REFERENCES user(uid)
);
```
- aid: UUID (PK)
- uid: FK to user
- acc_number: 12-digit account number
- balance: current balance

### deposit
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
- status: ACTIVE/MATURED/CLOSED
- rate: interest rate (e.g., 3.50)

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
- User 1:N Deposit
- User 1:N Savings

## Implementation Status
- user, account: **Implemented**
- deposit, savings: **Not implemented**
