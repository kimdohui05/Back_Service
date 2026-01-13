package com.example.bankservice.account.controller;

import com.example.bankservice.account.dto.*;
import com.example.bankservice.account.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AccountController 클래스
 *
 * 역할: 계좌 관련 API 요청을 받는 컨트롤러
 * - 계좌 개설, 조회, 입금, 출금, 송금 등
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 계좌 개설 API
     * POST /api/accounts/create
     *
     * @param request - 계좌 개설 요청 (uid, accPassword)
     * @return 개설된 계좌 정보
     */
    @PostMapping("/create")
    public Account createAccount(@RequestBody AccountCreateRequest request) {
        return accountService.createAccount(request);
    }

    /**
     * 계좌 조회 API (잔액 조회)
     * GET /api/accounts/{accNumber}
     *
     * @param accNumber - 계좌번호
     * @return 계좌 정보 (잔액 포함)
     */
    @GetMapping("/{accNumber}")
    public Account getAccountInfo(@PathVariable String accNumber) {
        return accountService.getAccountInfo(accNumber);
    }

    /**
     * 내 계좌 목록 조회 API
     * GET /api/accounts/my/{uid}
     *
     * @param uid - 유저 고유번호
     * @return 계좌 목록
     */
    @GetMapping("/my/{uid}")
    public List<Account> getMyAccounts(@PathVariable String uid) {
        return accountService.getMyAccounts(uid);
    }

    /**
     * 입금 API
     * POST /api/accounts/deposit
     *
     * @param request - 입금 요청 (accNumber, amount)
     * @return 성공/실패 메시지
     */
    @PostMapping("/deposit")
    public String deposit(@RequestBody DepositRequest request) {
        return accountService.deposit(request);
    }

    /**
     * 출금 API
     * POST /api/accounts/withdraw
     *
     * @param request - 출금 요청 (accNumber, accPassword, amount)
     * @return 성공/실패 메시지
     */
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody WithdrawRequest request) {
        return accountService.withdraw(request);
    }

    /**
     * 송금 API
     * POST /api/accounts/transfer
     *
     * @param request - 송금 요청 (fromAccNumber, toAccNumber, accPassword, amount)
     * @return 성공/실패 메시지
     */
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        return accountService.transfer(request);
    }
}
