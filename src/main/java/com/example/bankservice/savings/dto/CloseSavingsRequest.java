package com.example.bankservice.savings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CloseSavingsRequest DTO
 *
 * 역할: 적금 해지 요청 데이터
 * - 적금 계좌 해지 시 필요한 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseSavingsRequest {

    /**
     * accNumber: 적금 계좌번호
     * - 해지할 적금 계좌번호
     */
    private String accNumber;

    /**
     * accPassword: 적금 계좌 비밀번호
     * - 본인 확인용 비밀번호
     */
    private String accPassword;
}
