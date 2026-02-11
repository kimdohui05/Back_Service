package com.example.bankservice.savings.controller;

import com.example.bankservice.savings.dto.*;
import com.example.bankservice.savings.service.SavingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SavingsController 클래스
 *
 * 역할: 적금 계좌 관련 HTTP 요청 처리
 * - REST API 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/savings")
public class SavingsController {

    private final SavingsService savingsService;

    public SavingsController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

    /**
     * 적금 계좌 개설
     *
     * POST /api/savings/create
     * Body: { "uid": "...", "accPassword": "1234", "rate": 3.5, "period": 12, "mthlyDeposit": 100000 }
     *
     * @param request - 적금 계좌 개설 요청
     * @return 성공 메시지와 적금 계좌번호
     */
    @PostMapping("/create")
    public ResponseEntity<String> createSavings(@RequestBody SavingsCreateRequest request) {
        try {
            String result = savingsService.createSavings(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("적금 계좌 개설 실패: " + e.getMessage());
        }
    }

    /**
     * 적금 계좌 조회
     *
     * GET /api/savings/{accNumber}
     *
     * @param accNumber - 적금 계좌번호
     * @return 적금 계좌 정보
     */
    @GetMapping("/{accNumber}")
    public ResponseEntity<?> getSavingsInfo(@PathVariable String accNumber) {
        try {
            Savings savings = savingsService.getSavingsInfo(accNumber);
            return ResponseEntity.ok(savings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("적금 계좌 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 내 적금 계좌 목록 조회
     *
     * GET /api/savings/my/{uid}
     *
     * @param uid - 유저 고유번호
     * @return 적금 계좌 목록
     */
    @GetMapping("/my/{uid}")
    public ResponseEntity<?> getMySavings(@PathVariable String uid) {
        try {
            List<Savings> savingsList = savingsService.getMySavings(uid);
            return ResponseEntity.ok(savingsList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("적금 계좌 목록 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 적금 입금
     *
     * POST /api/savings/deposit
     * Body: { "accNumber": "9...", "amount": 100000 }
     *
     * @param request - 입금 요청
     * @return 성공 메시지
     */
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody SavingsDepositRequest request) {
        try {
            String result = savingsService.deposit(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("적금 입금 실패: " + e.getMessage());
        }
    }

    /**
     * 적금 해지
     *
     * POST /api/savings/close
     * Body: { "accNumber": "9...", "accPassword": "1234" }
     *
     * @param request - 해지 요청 (계좌번호, 비밀번호)
     * @return 성공 메시지와 해지 금액
     */
    @PostMapping("/close")
    public ResponseEntity<String> closeSavings(@RequestBody CloseSavingsRequest request) {
        try {
            String result = savingsService.closeSavings(
                    request.getAccNumber(),
                    request.getAccPassword()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("적금 해지 실패: " + e.getMessage());
        }
    }
}
