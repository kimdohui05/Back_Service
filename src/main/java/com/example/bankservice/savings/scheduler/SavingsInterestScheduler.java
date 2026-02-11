package com.example.bankservice.savings.scheduler;

import com.example.bankservice.savings.dto.Savings;
import com.example.bankservice.savings.repository.SavingsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * SavingsInterestScheduler 클래스 (적금 이자 스케줄러)
 *
 * 역할: 적금 계좌에 자동으로 이자를 적용하고 이자율을 관리하는 스케줄러
 *
 * 동작 방식:
 * - 매일 자정(00:00:00)에 자동으로 실행 (@Scheduled)
 * - 어제 입금한 계좌: 이자 적용
 * - 어제 입금하지 않은 계좌: 이자율 감소
 *
 * 이자 계산 공식:
 * - 이자 = floor(현재 잔액 × (현재 이자율 / 100))
 * - 새 잔액 = 현재 잔액 + 이자
 *
 * 이자율 감소 (입금하지 않은 날):
 * - 1개월(30일): 0.1%씩 감소
 * - 6개월(180일): 0.05%씩 감소
 * - 1년(365일): 0.01%씩 감소
 * - 최소값: 0% (0% 아래로는 떨어지지 않음)
 *
 * 예시 1 (입금한 경우 - 6개월, 현재 이자율 1.3%):
 * - 적금 잔액: 100,000원
 * - 어제 일일 납입액(10,000원) 입금함 → 110,000원
 * - 자정이 지나면: 110,000 × 0.013 = 1,430원 이자 추가
 * - 새 잔액: 110,000 + 1,430 = 111,430원
 * - 현재 이자율: 1.3% (변동 없음)
 *
 * 예시 2 (입금 안 한 경우 - 6개월, 현재 이자율 1.3%):
 * - 적금 잔액: 111,430원 (변동 없음)
 * - 현재 이자율: 1.3% - 0.05% = 1.25% (감소)
 *
 * 예시 3 (입금 안 한 경우 - 1년, 현재 이자율 0.05%):
 * - 현재 이자율: 0.05% - 0.01% = 0.04% (감소)
 *
 * 예시 4 (입금 안 한 경우 - 현재 이자율 0.00%):
 * - 현재 이자율: 0.00% (더 이상 감소 안 함)
 *
 * 테스트 설정:
 * - 프로덕션: @Scheduled(cron = "0 0 0 * * *") // 매일 자정
 * - 테스트: @Scheduled(fixedRate = 60000) // 1분마다 (테스트용)
 */
@Component
public class SavingsInterestScheduler {

    private final SavingsRepository savingsRepository;

    // 기간별 이자율 감소량
    private static final double RATE_DECREASE_1_MONTH = 0.1;    // 1개월: 0.1%씩 감소
    private static final double RATE_DECREASE_6_MONTHS = 0.05;  // 6개월: 0.05%씩 감소
    private static final double RATE_DECREASE_1_YEAR = 0.01;    // 1년: 0.01%씩 감소

    // 기간 상수
    private static final int PERIOD_1_MONTH = 30;    // 1개월
    private static final int PERIOD_6_MONTHS = 180;  // 6개월
    private static final int PERIOD_1_YEAR = 365;    // 1년

    public SavingsInterestScheduler(SavingsRepository savingsRepository) {
        this.savingsRepository = savingsRepository;
    }

    /**
     * 매일 자정에 적금 계좌에 이자 적용 및 이자율 관리
     *
     * @Scheduled 설명:
     * - cron: cron 표현식 사용
     * - "0 0 0 * * *" = 초 분 시 일 월 요일
     * - 매일 00시 00분 00초에 실행
     *
     * 테스트용으로 빠르게 하려면:
     * - @Scheduled(fixedRate = 60000) // 1분마다
     * - @Scheduled(fixedRate = 10000) // 10초마다
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 00:00:00
    public void applyInterestToSavings() {
        // 어제 날짜 계산
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // System.out.println("[SavingsInterestScheduler] 적금 처리 시작");
        // System.out.println("[SavingsInterestScheduler] 기준 날짜(어제): " + yesterday);

        // 1. 어제 입금한 계좌에 이자 적용
        applyInterestToDepositedAccounts(yesterday);

        // 2. 어제 입금하지 않은 계좌의 이자율 감소
        decreaseInterestRateForNonDepositedAccounts(yesterday);

        // System.out.println("[SavingsInterestScheduler] 적금 처리 완료");
    }

    /**
     * 어제 입금한 계좌에 이자 적용
     *
     * @param yesterday - 어제 날짜
     */
    private void applyInterestToDepositedAccounts(LocalDate yesterday) {
        // 어제 입금한 ACTIVE 상태의 적금 계좌들 조회
        List<Savings> savingsList = savingsRepository.findActiveWithDepositOnDate(yesterday);

        // System.out.println("[이자 적용] 대상 계좌 수: " + savingsList.size());

        int updatedCount = 0;

        // 각 적금 계좌에 이자 적용
        for (Savings savings : savingsList) {
            try {
                // 현재 잔액
                long currentBalance = savings.getBalance();

                // 현재 이자율 (입금하지 않은 날로 인해 감소했을 수 있음)
                double currentRate = savings.getCurrentRate();

                // 이자 계산 (소수점 버림)
                long interest = (long) Math.floor(currentBalance * (currentRate / 100.0));

                // 새 잔액 = 현재 잔액 + 이자
                long newBalance = currentBalance + interest;

                // 잔액 업데이트 (원금은 변경 안 됨)
                savingsRepository.updateBalance(savings.getAccNumber(), newBalance);

                updatedCount++;

                // System.out.println(String.format(
                //         "[이자 적용] 계좌 %s: %d원 → %d원 (이자: %d원, 현재 이자율: %.2f%%, 원금: %d원)",
                //         savings.getAccNumber(),
                //         currentBalance,
                //         newBalance,
                //         interest,
                //         currentRate,
                //         savings.getPrincipal()
                // ));

            } catch (Exception e) {
                // System.err.println("[이자 적용 실패] 계좌 " + savings.getAccNumber() + ": " + e.getMessage());
            }
        }

        // System.out.println("[이자 적용 완료] 업데이트된 계좌 수: " + updatedCount);
    }

    /**
     * 어제 입금하지 않은 계좌의 이자율 감소
     *
     * @param yesterday - 어제 날짜
     */
    private void decreaseInterestRateForNonDepositedAccounts(LocalDate yesterday) {
        // 어제 입금하지 않은 ACTIVE 상태의 적금 계좌들 조회
        List<Savings> savingsList = savingsRepository.findActiveWithoutDepositOnDate(yesterday);

        // System.out.println("[이자율 감소] 대상 계좌 수: " + savingsList.size());

        int decreasedCount = 0;

        for (Savings savings : savingsList) {
            try {
                // 현재 이자율
                double currentRate = savings.getCurrentRate();

                // 이미 0%면 더 이상 감소 안 함
                if (currentRate <= 0.0) {
                    continue;
                }

                // 기간에 따른 감소량 계산
                double decreaseAmount;
                if (savings.getPeriod() == PERIOD_1_MONTH) {
                    decreaseAmount = RATE_DECREASE_1_MONTH; // 1개월: 0.1%
                } else if (savings.getPeriod() == PERIOD_6_MONTHS) {
                    decreaseAmount = RATE_DECREASE_6_MONTHS; // 6개월: 0.05%
                } else if (savings.getPeriod() == PERIOD_1_YEAR) {
                    decreaseAmount = RATE_DECREASE_1_YEAR; // 1년: 0.01%
                } else {
                    continue; // 알 수 없는 기간이면 스킵
                }

                // 새 이자율 계산
                double newRate = currentRate - decreaseAmount;

                // 0% 아래로 떨어지지 않도록 제한
                if (newRate < 0.0) {
                    newRate = 0.0;
                }

                // 이자율 업데이트
                savingsRepository.updateCurrentRate(savings.getAccNumber(), newRate);

                decreasedCount++;

                // System.out.println(String.format(
                //         "[이자율 감소] 계좌 %s: %.2f%% → %.2f%% (%.2f%% 감소, 기간: %d일)",
                //         savings.getAccNumber(),
                //         currentRate,
                //         newRate,
                //         decreaseAmount,
                //         savings.getPeriod()
                // ));

            } catch (Exception e) {
                // System.err.println("[이자율 감소 실패] 계좌 " + savings.getAccNumber() + ": " + e.getMessage());
            }
        }

        // System.out.println("[이자율 감소 완료] 업데이트된 계좌 수: " + decreasedCount);
    }

    /**
     * 테스트용 스케줄러 (1분마다 실행)
     *
     * 사용 방법:
     * 1. 위의 @Scheduled(cron = "0 0 0 * * *")를 주석 처리
     * 2. 아래 메서드의 주석을 해제
     * 3. 테스트 후 다시 원래대로 복구
     */
    /*
    @Scheduled(fixedRate = 60000) // 1분마다 실행 (테스트용)
    public void applyInterestToSavingsTest() {
        // 테스트를 위해 오늘 입금한 계좌에도 이자 적용
        LocalDate today = LocalDate.now();

        List<Savings> savingsList = savingsRepository.findActiveWithDepositOnDate(today);

        System.out.println("[TEST] 적금 이자 적용 시작 - 대상 계좌 수: " + savingsList.size());

        for (Savings savings : savingsList) {
            try {
                long currentBalance = savings.getBalance();
                double interestRate = savings.getRate();
                long interest = (long) Math.floor(currentBalance * (interestRate / 100.0));
                long newBalance = currentBalance + interest;

                savingsRepository.updateBalance(savings.getAccNumber(), newBalance);

                System.out.println(String.format(
                        "[TEST] 계좌 %s: %d원 → %d원 (이자: %d원, +%.1f%%, 원금: %d원)",
                        savings.getAccNumber(),
                        currentBalance,
                        newBalance,
                        interest,
                        interestRate,
                        savings.getPrincipal()
                ));
            } catch (Exception e) {
                System.err.println("[TEST] 오류: " + e.getMessage());
            }
        }

        System.out.println("[TEST] 적금 이자 적용 완료");
    }
    */
}
