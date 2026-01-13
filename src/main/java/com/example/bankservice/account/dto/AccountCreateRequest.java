package com.example.bankservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 계좌 개설 요청 DTO
 *
 * 역할: 새 계좌를 만들 때 필요한 정보를 담는 클래스
 * - 계좌번호는 자동 생성
 * - 초기 잔액은 0원 고정
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequest {

    /**
     * uid: 유저 고유번호
     * - 어떤 유저가 계좌를 개설하는지 식별
     */
    private String uid;

    /**
     * accPassword: 계좌 비밀번호
     * - 4자리 숫자
     * - 사용자가 직접 설정
     */
    private String accPassword;
}
