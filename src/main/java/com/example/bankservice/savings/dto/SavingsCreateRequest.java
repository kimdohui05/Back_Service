package com.example.bankservice.savings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SavingsCreateRequest DTO
 *
 * 역할: 적금 계좌 개설 요청 데이터
 * - 클라이언트로부터 적금 개설 시 필요한 정보를 받음
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsCreateRequest {

    /**
     * uid: 유저 고유번호
     * - 적금 계좌를 개설할 유저의 uid
     */
    private String uid;

    /**
     * accPassword: 적금 계좌 비밀번호
     * - 4자리 숫자
     */
    private String accPassword;

    /**
     * rate: 적금 이자율
     * - 연이율 (예: 3.5%)
     */
    private Double rate;

    /**
     * period: 적금 기간 (일 수)
     * - 6개월: 180일 (이자율 1.3%)
     * - 1년: 365일 (이자율 1.5%)
     */
    private Integer period;

    /**
     * dailyDeposit: 일일 납입액
     * - 매일 납입해야 하는 금액
     * - 선택 가능한 금액: 10000, 30000, 50000, 100000
     */
    private Integer dailyDeposit;
}
