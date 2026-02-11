package com.example.bankservice.savings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Savings DTO (Data Transfer Object)
 *
 * 역할: 적금 계좌 정보를 담는 클래스
 * - 적금 계좌 개설, 조회 시 사용
 * - 데이터베이스의 savings 테이블과 1:1 매칭됨
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Savings {

    /**
     * sid: 적금 계좌 고유번호
     * - 데이터베이스에서 자동으로 생성됨 (UUID 사용)
     * - Primary Key
     * - DB 컬럼: sid (VARCHAR(36), PK, NN)
     */
    private String sid;

    /**
     * uid: 유저 고유번호 (외래키)
     * - 이 적금 계좌를 소유한 유저의 uid
     * - DB 컬럼: uid (VARCHAR(36), FK, NN)
     */
    private String uid;

    /**
     * accNumber: 적금 계좌번호
     * - 12자리 숫자
     * - 9로 시작 (일반 계좌는 0으로 시작)
     * - 적금 계좌 개설 시 자동 생성
     * - DB 컬럼: acc_number (VARCHAR(12), NN)
     */
    private String accNumber;

    /**
     * accPassword: 적금 계좌 비밀번호
     * - 4자리 숫자
     * - 적금 해지, 입금 시 필요
     * - DB 컬럼: acc_password (VARCHAR(4), NN)
     */
    private String accPassword;

    /**
     * rate: 적금 초기 이자율 (변경되지 않음)
     * - 6개월: 1.3%
     * - 1년: 1.5%
     * - DB 컬럼: rate (DECIMAL(4, 2), NN)
     */
    private Double rate;

    /**
     * currentRate: 현재 적용 중인 이자율
     * - 초기값: rate와 동일
     * - 입금하지 않은 날마다 감소
     *   - 6개월: 0.05%씩 감소
     *   - 1년: 0.01%씩 감소
     * - 최소값: 0%
     * - DB 컬럼: current_rate (DECIMAL(4, 2), NN)
     */
    private Double currentRate;

    /**
     * startDate: 적금 시작일
     * - 적금 개설 날짜
     * - DB 컬럼: start_date (DATE, NN)
     */
    private LocalDate startDate;

    /**
     * status: 적금 상태
     * - ACTIVE: 진행 중
     * - MATURED: 만기
     * - CLOSED: 해지됨
     * - DB 컬럼: status (ENUM('ACTIVE', 'MATURED', 'CLOSED'), NN)
     */
    private String status;

    /**
     * balance: 현재 잔액 (적립 금액 + 이자)
     * - 초기 잔액: 0원
     * - 매일 입금과 이자로 증가
     * - DB 컬럼: balance (DECIMAL(20, 2), NN)
     */
    private Long balance;

    /**
     * principal: 원금 (이자 제외한 순수 납입 금액)
     * - 초기 원금: 0원
     * - 매일 입금으로만 증가 (이자는 포함 안 됨)
     * - 중도 해지 시 이 금액만 반환
     * - DB 컬럼: principal (DECIMAL(20, 2), NN)
     */
    private Long principal;

    /**
     * period: 적금 기간 (일 수)
     * - 6개월: 180일 (이자율 1.3%)
     * - 1년: 365일 (이자율 1.5%)
     * - DB 컬럼: period (INT, NN)
     */
    private Integer period;

    /**
     * dailyDeposit: 일일 납입액
     * - 매일 납입해야 하는 금액
     * - 선택 가능한 금액: 10000, 30000, 50000, 100000
     * - DB 컬럼: mthly_deposit (INT, NN) - 컬럼명은 mthly_deposit이지만 일일 납입액으로 사용
     */
    private Integer dailyDeposit;

    /**
     * lastDepositDate: 마지막 입금 날짜
     * - 하루에 한 번만 입금 가능하도록 체크
     * - 이자 적용 여부 확인용
     * - DB 컬럼: last_deposit_date (DATE)
     */
    private java.time.LocalDate lastDepositDate;
}
