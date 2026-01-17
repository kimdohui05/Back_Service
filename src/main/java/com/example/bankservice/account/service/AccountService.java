package com.example.bankservice.account.service;

import com.example.bankservice.account.dto.*;
import com.example.bankservice.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AccountService 클래스
 *
 * 역할: 계좌 관련 비즈니스 로직 처리
 * - 계좌 개설, 입금, 출금, 송금, 조회 등
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 계좌 개설
     *
     * @param request - 계좌 개설 요청 (uid, accPassword)
     * @return 개설된 계좌 정보
     */
    public Account createAccount(AccountCreateRequest request) {
        // 12자리 계좌번호 생성
        String accNumber = accountRepository.generateAccountNumber();

        // Account 객체 생성 (초기 잔액 0원)
        Account account = Account.builder()
                .uid(request.getUid())
                .accNumber(accNumber)
                .accPassword(request.getAccPassword())
                .balance(0L) // 초기 잔액 0원
                .build();

        // 데이터베이스에 저장
        accountRepository.save(account);

        return account;
    }

    /**
     * 계좌 조회 (잔액 조회)
     *
     * @param accNumber - 계좌번호
     * @return 계좌 정보
     */
    public Account getAccountInfo(String accNumber) {
        Account account = accountRepository.findByAccNumber(accNumber);

        if (account == null) {
            throw new RuntimeException("계좌를 찾을 수 없습니다");
        }

        return account;
    }

    /**
     * 유저의 모든 계좌 조회
     *
     * @param uid - 유저 고유번호
     * @return 계좌 목록
     */
    public List<Account> getMyAccounts(String uid) {
        return accountRepository.findByUid(uid);
    }

    /**
     * 입금
     *
     * @param request - 입금 요청 (accNumber, amount)
     * @return 성공 메시지
     */
    public String deposit(DepositRequest request) {
        // 계좌 찾기
        Account account = accountRepository.findByAccNumber(request.getAccNumber());
        if (account == null) {
            return "계좌를 찾을 수 없습니다";
        }

        // 입금액 검증
        if (request.getAmount() <= 0) {
            return "입금액은 0원보다 커야 합니다";
        }

        // 새 잔액 계산 (기존 잔액 + 입금액)
        Long newBalance = account.getBalance() + request.getAmount();

        // 잔액 업데이트
        accountRepository.updateBalance(request.getAccNumber(), newBalance);

        return "입금 성공";
    }

    /**
     * 출금
     *
     * @param request - 출금 요청 (accNumber, accPassword, amount)
     * @return 성공/실패 메시지
     */
    public String withdraw(WithdrawRequest request) {
        // 계좌 찾기
        Account account = accountRepository.findByAccNumber(request.getAccNumber());
        if (account == null) {
            return "계좌를 찾을 수 없습니다";
        }

        // 계좌 비밀번호 확인
        if (!account.getAccPassword().equals(request.getAccPassword())) {
            return "계좌 비밀번호가 틀렸습니다";
        }

        // 출금액 검증
        if (request.getAmount() <= 0) {
            return "출금액은 0원보다 커야 합니다";
        }

        // 잔액 확인
        if (account.getBalance() < request.getAmount()) {
            return "잔액이 부족합니다";
        }

        // 새 잔액 계산 (기존 잔액 - 출금액)
        Long newBalance = account.getBalance() - request.getAmount();

        // 잔액 업데이트
        accountRepository.updateBalance(request.getAccNumber(), newBalance);

        return "출금 성공";
    }

    /**
     * 송금
     * @Transactional: 송금 중 에러 발생 시 모든 작업 취소 (원자성 보장)
     *
     * @param request - 송금 요청 (fromAccNumber, toAccNumber, accPassword, amount)
     * @return 성공/실패 메시지
     */
    @Transactional
    public String transfer(TransferRequest request) {
        // 출금(보내는) 계좌 찾기
        Account fromAccount = accountRepository.findByAccNumber(request.getFromAccNumber());
        if (fromAccount == null) {
            return "출금 계좌를 찾을 수 없습니다";
        }

        // 입금(받는) 계좌 찾기
        Account toAccount = accountRepository.findByAccNumber(request.getToAccNumber());
        if (toAccount == null) {
            return "입금 계좌를 찾을 수 없습니다";
        }

        // 계좌 비밀번호 확인
        if (!fromAccount.getAccPassword().equals(request.getAccPassword())) {
            return "계좌 비밀번호가 틀렸습니다";
        }

        // 송금액 검증
        if (request.getAmount() <= 0) {
            return "송금액은 0원보다 커야 합니다";
        }

        // 잔액 확인
        if (fromAccount.getBalance() < request.getAmount()) {
            return "잔액이 부족합니다";
        }

        // 출금 계좌 잔액 감소
        Long newFromBalance = fromAccount.getBalance() - request.getAmount();
        accountRepository.updateBalance(request.getFromAccNumber(), newFromBalance);

        // 입금 계좌 잔액 증가
        Long newToBalance = toAccount.getBalance() + request.getAmount();
        accountRepository.updateBalance(request.getToAccNumber(), newToBalance);

        return "송금 성공";
    }
}
