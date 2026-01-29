package com.example.bankservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
     * balance: 현재 잔액 (정수)
     * - 초기 잔액: 0원
     * - 입금/출금/송금으로 변동
     * - 1시간마다 자동으로 1% 이자 추가
     * - DB 컬럼: balance (BIGINT, NN)
     */
    private Long balance;

    /**
     * lastInterestUpdate: 마지막 이자 적용 시각
     * - 이자 스케줄러가 마지막으로 이자를 적용한 시각
     * - 이 시각부터 현재까지의 경과 시간으로 이자 계산
     * - DB 컬럼: last_interest_update (DATETIME, DEFAULT CURRENT_TIMESTAMP)
     *
     * 왜 필요한가?
     * - 서버가 다운되었다가 재시작해도 누락된 이자를 계산할 수 있음
     * - 예: 14:00에 마지막 이자 적용 후 서버 다운 → 17:00에 재시작
     *   → 3시간 경과 인식하여 3시간치 이자 한 번에 적용
     *
     * 동작 방식:
     * - 스케줄러가 계좌 조회 시 lastInterestUpdate부터 현재까지 경과 시간 계산
     * - 경과 시간만큼 복리 이자 적용
     * - 적용 후 lastInterestUpdate를 현재 시각으로 업데이트
     */
    private LocalDateTime lastInterestUpdate;
}
