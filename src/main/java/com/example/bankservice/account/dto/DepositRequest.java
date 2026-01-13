package com.example.bankservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 입금 요청 DTO
 *
 * 역할: 계좌에 입금할 때 필요한 정보를 담는 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {

    /**
     * accNumber: 입금할 계좌번호
     * - 12자리 숫자
     */
    private String accNumber;

    /**
     * amount: 입금액
     * - 양수만 가능
     */
    private BigDecimal amount;
}
