package com.example.bankservice.savings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SavingsDepositRequest DTO
 *
 * 역할: 적금 입금 요청 데이터
 * - 적금 계좌에 입금할 때 필요한 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsDepositRequest {

    /**
     * accNumber: 적금 계좌번호
     * - 입금할 적금 계좌번호
     */
    private String accNumber;

    /**
     * amount: 입금액
     * - 입금할 금액
     */
    private Long amount;
}
