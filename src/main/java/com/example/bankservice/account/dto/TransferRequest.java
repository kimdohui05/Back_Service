package com.example.bankservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 송금 요청 DTO
 *
 * 역할: 다른 계좌로 송금할 때 필요한 정보를 담는 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    /**
     * fromAccNumber: 출금(보내는) 계좌번호
     * - 내 계좌번호
     */
    private String fromAccNumber;

    /**
     * toAccNumber: 입금(받는) 계좌번호
     * - 상대방 계좌번호
     */
    private String toAccNumber;

    /**
     * accPassword: 출금 계좌 비밀번호
     * - 4자리 숫자
     * - 본인 확인용
     */
    private String accPassword;

    /**
     * amount: 송금액 (정수)
     * - 양수만 가능
     * - 내 계좌 잔액보다 클 수 없음
     */
    private Long amount;
}
