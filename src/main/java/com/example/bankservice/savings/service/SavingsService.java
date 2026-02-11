package com.example.bankservice.savings.service;

import com.example.bankservice.savings.dto.*;
import com.example.bankservice.savings.repository.SavingsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * SavingsService 클래스
 *
 * 역할: 적금 계좌 관련 비즈니스 로직 처리
 * - 적금 계좌 개설, 입금, 조회, 해지 등
 */
@Service
public class SavingsService {

    private final SavingsRepository savingsRepository;

    // 선택 가능한 일일 납입액 옵션
    private static final List<Integer> ALLOWED_DAILY_DEPOSITS = Arrays.asList(10000, 30000, 50000, 100000);

    // 선택 가능한 기간 (일 수)
    private static final int PERIOD_1_MONTH = 30;    // 1개월
    private static final int PERIOD_6_MONTHS = 180;  // 6개월
    private static final int PERIOD_1_YEAR = 365;    // 1년

    // 기간별 이자율
    private static final double RATE_1_MONTH = 1.1;    // 1개월: 1.1%
    private static final double RATE_6_MONTHS = 1.3;  // 6개월: 1.3%
    private static final double RATE_1_YEAR = 1.5;    // 1년: 1.5%

    public SavingsService(SavingsRepository savingsRepository) {
        this.savingsRepository = savingsRepository;
    }

    /**
     * 기간에 따른 이자율 반환
     *
     * @param period - 적금 기간 (일 수)
     * @return 이자율
     */
    private double getInterestRate(int period) {
        if (period == PERIOD_1_MONTH) {
            return RATE_1_MONTH;
        } else if (period == PERIOD_6_MONTHS) {
            return RATE_6_MONTHS;
        } else if (period == PERIOD_1_YEAR) {
            return RATE_1_YEAR;
        }
        return 0.0;
    }

    /**
     * 적금 계좌 개설
     *
     * @param request - 적금 계좌 개설 요청 (uid, accPassword, period, dailyDeposit)
     * @return 성공 메시지와 적금 계좌번호
     */
    public String createSavings(SavingsCreateRequest request) {
        // 일일 납입액 검증
        if (!ALLOWED_DAILY_DEPOSITS.contains(request.getDailyDeposit())) {
            return "일일 납입액은 10,000원, 30,000원, 50,000원, 100,000원 중에서 선택해야 합니다";
        }

        // 기간 검증
        if (request.getPeriod() != PERIOD_1_MONTH && request.getPeriod() != PERIOD_6_MONTHS && request.getPeriod() != PERIOD_1_YEAR) {
            return "적금 기간은 1개월(30일), 6개월(180일) 또는 1년(365일) 중에서 선택해야 합니다";
        }

        // 기간에 따른 이자율 설정
        double interestRate = getInterestRate(request.getPeriod());

        // 12자리 적금 계좌번호 생성 (9로 시작)
        String accNumber = savingsRepository.generateSavingsAccountNumber();

        // Savings 객체 생성 (초기 잔액 0원, 원금 0원, 상태 ACTIVE)
        Savings savings = Savings.builder()
                .uid(request.getUid())
                .accNumber(accNumber)
                .accPassword(request.getAccPassword())
                .rate(interestRate) // 기간에 따른 초기 이자율
                .currentRate(interestRate) // 현재 이자율 (초기값은 rate와 동일)
                .startDate(LocalDate.now()) // 오늘 날짜
                .status("ACTIVE") // 진행 중
                .balance(0L) // 초기 잔액 0원
                .principal(0L) // 초기 원금 0원
                .period(request.getPeriod())
                .dailyDeposit(request.getDailyDeposit())
                .lastDepositDate(null) // 아직 입금 안 함
                .build();

        // 데이터베이스에 저장
        savingsRepository.save(savings);

        // 해당 유저의 모든 적금 계좌 조회
        List<Savings> savingsList = savingsRepository.findByUid(request.getUid());

        // 적금 계좌번호들을 문자열로 변환
        String savingsNumbers = savingsList.stream()
                .map(Savings::getAccNumber)
                .reduce((a, b) -> a + ", " + b)
                .orElse("없음");

        String periodText;
        if (request.getPeriod() == PERIOD_1_MONTH) {
            periodText = "1개월";
        } else if (request.getPeriod() == PERIOD_6_MONTHS) {
            periodText = "6개월";
        } else {
            periodText = "1년";
        }

        return "적금 계좌 개설에 성공했습니다\n" +
                "기간: " + periodText + " (" + request.getPeriod() + "일)\n" +
                "일일 납입액: " + request.getDailyDeposit() + "원\n" +
                "이자율: " + interestRate + "% (매일 자정 적용)\n" +
                "현재 보유하고 있는 적금 계좌번호 : " + savingsNumbers;
    }

    /**
     * 적금 계좌 조회
     *
     * @param accNumber - 적금 계좌번호
     * @return 적금 계좌 정보
     */
    public Savings getSavingsInfo(String accNumber) {
        Savings savings = savingsRepository.findByAccNumber(accNumber);

        if (savings == null) {
            throw new RuntimeException("적금 계좌를 찾을 수 없습니다");
        }

        return savings;
    }

    /**
     * 유저의 모든 적금 계좌 조회
     *
     * @param uid - 유저 고유번호
     * @return 적금 계좌 목록
     */
    public List<Savings> getMySavings(String uid) {
        return savingsRepository.findByUid(uid);
    }

    /**
     * 적금 입금
     *
     * @param request - 입금 요청 (accNumber, amount)
     * @return 성공 메시지
     */
    public String deposit(SavingsDepositRequest request) {
        // 적금 계좌 찾기
        Savings savings = savingsRepository.findByAccNumber(request.getAccNumber());
        if (savings == null) {
            return "적금 계좌를 찾을 수 없습니다";
        }

        // 상태 확인 (ACTIVE 상태만 입금 가능)
        if (!"ACTIVE".equals(savings.getStatus())) {
            return "입금할 수 없는 적금 계좌입니다 (상태: " + savings.getStatus() + ")";
        }

        // 입금액이 일일 납입액을 초과하는지 확인
        if (request.getAmount() > savings.getDailyDeposit().longValue()) {
            return "입금액은 일일 납입액(" + savings.getDailyDeposit() + "원)을 초과할 수 없습니다\n" +
                    "입금 시도 금액: " + request.getAmount() + "원";
        }

        // 오늘 날짜 확인
        LocalDate today = LocalDate.now();

        // 하루에 한 번만 입금 가능
        if (savings.getLastDepositDate() != null && savings.getLastDepositDate().equals(today)) {
            return "오늘은 이미 입금하셨습니다. 내일 다시 입금해주세요";
        }

        // 새 잔액 계산 (기존 잔액 + 입금액)
        Long newBalance = savings.getBalance() + request.getAmount();

        // 새 원금 계산 (기존 원금 + 입금액)
        Long newPrincipal = savings.getPrincipal() + request.getAmount();

        // 입금액이 일일 납입액 이상인지 확인
        boolean isFullDeposit = request.getAmount() >= savings.getDailyDeposit().longValue();

        if (isFullDeposit) {
            // 일일 납입액 이상 입금: 잔액, 원금, 마지막 입금 날짜 모두 업데이트
            savingsRepository.updateBalanceAndPrincipalAndDepositDate(
                    request.getAccNumber(),
                    newBalance,
                    newPrincipal,
                    today
            );

            return "적금 입금 성공 ✅\n" +
                    "입금액: " + request.getAmount() + "원\n" +
                    "현재 잔액: " + newBalance + "원 (이자 포함)\n" +
                    "현재 원금: " + newPrincipal + "원 (이자 제외)\n" +
                    "상태: 일일 납입액 충족 - 내일 자정에 이자 적용 예정\n" +
                    "다음 입금 가능 날짜: " + today.plusDays(1);
        } else {
            // 일일 납입액 미만 입금: 잔액과 원금만 업데이트, 마지막 입금 날짜는 업데이트 안 함
            savingsRepository.updateBalanceAndPrincipal(
                    request.getAccNumber(),
                    newBalance,
                    newPrincipal
            );

            return "적금 입금 완료 ⚠️\n" +
                    "입금액: " + request.getAmount() + "원\n" +
                    "현재 잔액: " + newBalance + "원 (이자 포함)\n" +
                    "현재 원금: " + newPrincipal + "원 (이자 제외)\n" +
                    "경고: 일일 납입액(" + savings.getDailyDeposit() + "원) 미달\n" +
                    "→ 내일 자정에 이자 적용 안 됨\n" +
                    "→ 현재 이자율 감소 예정\n" +
                    "다음 입금 가능 날짜: " + today.plusDays(1);
        }
    }

    /**
     * 적금 해지 (중도 해지)
     *
     * @param accNumber - 적금 계좌번호
     * @param accPassword - 적금 계좌 비밀번호
     * @return 성공 메시지와 해지 금액
     */
    public String closeSavings(String accNumber, String accPassword) {
        // 적금 계좌 찾기
        Savings savings = savingsRepository.findByAccNumber(accNumber);
        if (savings == null) {
            return "적금 계좌를 찾을 수 없습니다";
        }

        // 계좌 비밀번호 확인
        if (!savings.getAccPassword().equals(accPassword)) {
            return "적금 계좌 비밀번호가 틀렸습니다";
        }

        // 이미 해지된 계좌인지 확인
        if ("CLOSED".equals(savings.getStatus())) {
            return "이미 해지된 적금 계좌입니다";
        }

        // 중도 해지: 원금만 반환 (이자는 모두 취소)
        Long returnAmount = savings.getPrincipal(); // 원금만 반환
        Long cancelledInterest = savings.getBalance() - savings.getPrincipal(); // 취소된 이자

        // 상태를 CLOSED로 변경
        savingsRepository.updateStatus(accNumber, "CLOSED");

        return "적금 해지 성공 (중도 해지)\n" +
                "반환 금액: " + returnAmount + "원 (원금만 반환)\n" +
                "취소된 이자: " + cancelledInterest + "원\n" +
                "원래 잔액: " + savings.getBalance() + "원\n" +
                "※ 중도 해지로 인해 이자는 모두 취소되었습니다";
    }
}
