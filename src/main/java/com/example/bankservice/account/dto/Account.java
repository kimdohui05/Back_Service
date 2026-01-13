package com.example.bankservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Account DTO (Data Transfer Object)
 *
 * 역할: 일반 입출금 계좌 정보를 담는 클래스
 * - 계좌 개설, 조회 시 사용
 * - 데이터베이스의 account 테이블과 1:1 매칭됨
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    /**
     * aid: 계좌 고유번호
     * - 데이터베이스에서 자동으로 생성됨 (UUID 사용)
     * - Primary Key
     * - DB 컬럼: aid (VARCHAR(36), PK, NN)
     */
    private String aid;

    /**
     * uid: 유저 고유번호 (외래키)
     * - 이 계좌를 소유한 유저의 uid
     * - DB 컬럼: uid (VARCHAR(36), FK, NN)
     */
    private String uid;

    /**
     * accNumber: 계좌번호
     * - 12자리 숫자
     * - 계좌 개설 시 자동 생성
     * - DB 컬럼: acc_number (VARCHAR(12), NN)
     */
    private String accNumber;

    /**
     * accPassword: 계좌 비밀번호
     * - 4자리 숫자
     * - 출금, 송금 시 필요
     * - DB 컬럼: acc_password (VARCHAR(4), NN)
     */
    private String accPassword;

    /**
     * balance: 현재 잔액
     * - 초기 잔액: 0원
     * - 입금/출금/송금으로 변동
     * - DB 컬럼: balance (DECIMAL(20, 2), NN)
     */
    private BigDecimal balance;
}
