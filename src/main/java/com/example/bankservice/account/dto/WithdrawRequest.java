package com.example.bankservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 출금 요청 DTO
 *
 * 역할: 계좌에서 출금할 때 필요한 정보를 담는 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {

    /**
     * accNumber: 출금할 계좌번호
     * - 12자리 숫자
     */
    private String accNumber;

    /**
     * accPassword: 계좌 비밀번호
     * - 4자리 숫자
     * - 본인 확인용
     */
    private String accPassword;

    /**
     * amount: 출금액
     * - 양수만 가능
     * - 잔액보다 클 수 없음
     */
    private BigDecimal amount;
}
