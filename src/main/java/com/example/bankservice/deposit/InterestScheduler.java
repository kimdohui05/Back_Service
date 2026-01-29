package com.example.bankservice.deposit;

import com.example.bankservice.account.dto.Account;
import com.example.bankservice.account.repository.AccountRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**1
 * InterestScheduler 클래스 (이자 스케줄러)
 *
 * 역할: 모든 계좌에 자동으로 이자를 적용하는 스케줄러
 *
 * 동작 방식:
 * - 1시간마다 자동으로 실행 (@Scheduled)
 * - 모든 계좌를 조회
 * - 각 계좌의 마지막 이자 적용 시각부터 현재까지 경과 시간 계산
 * - 경과 시간만큼 복리 이자 적용 (시간당 1%)
 * - 잔액과 마지막 이자 적용 시각 업데이트
 *
 * 이자 계산 공식:
 * - 새 잔액 = floor(현재 잔액 × 1.01^경과시간)
 *
 * 예시:
 * - 계좌 잔액: 100,000원, 마지막 업데이트: 14:00
 * - 현재 시각: 17:00 (3시간 경과)
 * - 새 잔액: floor(100,000 × 1.01^3) = floor(103,030.1) = 103,030원
 *
 * 서버 다운 복구:
 * - 서버가 다운되어도 재시작 시 누락된 이자 자동 계산
 * - lastInterestUpdate를 기준으로 하므로 손실 없음
 *
 * 테스트 설정:
 * - 프로덕션: @Scheduled(fixedRate = 3600000) // 1시간
 * - 테스트: @Scheduled(fixedRate = 60000) // 1분
 */
@Component
public class InterestScheduler {

    private final AccountRepository accountRepository;

    public InterestScheduler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 1시간마다 모든 계좌에 이자 적용
     *
     * @Scheduled 설명:
     * - fixedRate: 이전 실행 시작 시점부터 N밀리초 후에 다음 실행
     * - 3600000ms = 3600초 = 60분 = 1시간
     *
     * 테스트용으로 빠르게 하려면:
     * - fixedRate = 60000 (1분마다) 또는
     * - fixedRate = 10000 (10초마다)
     */
    @Scheduled(fixedRate = 3600000) // 1시간 = 3600000ms
    public void applyInterestToAllAccounts() {
        // 현재 시각 기록
        LocalDateTime now = LocalDateTime.now();

        // 모든 계좌 조회
        List<Account> accounts = accountRepository.findAll();

        System.out.println("[InterestScheduler] 이자 적용 시작 - 대상 계좌 수: " + accounts.size());

        int updatedCount = 0;

        // 각 계좌에 이자 적용
        for (Account account : accounts) {
            try {
                // 마지막 이자 업데이트 시각 확인
                LocalDateTime lastUpdate = account.getLastInterestUpdate();

                // lastInterestUpdate가 null이면 현재 시각으로 초기화만 하고 이자 적용 안 함
                if (lastUpdate == null) {
                    accountRepository.updateBalanceAndInterestTime(
                            account.getAccNumber(),
                            account.getBalance(),
                            now
                    );
                    continue;
                }

                // 경과 시간 계산 (시간 단위)
                long hoursElapsed = ChronoUnit.HOURS.between(lastUpdate, now);

                // 1시간 미만 경과면 이자 적용 안 함
                if (hoursElapsed < 1) {
                    continue;
                }

                // 복리 이자 계산: balance × 1.01^hoursElapsed
                double newBalanceDouble = account.getBalance() * Math.pow(1.01, hoursElapsed);

                // 소수점 버림
                long newBalance = (long) Math.floor(newBalanceDouble);

                // 잔액이 변경된 경우에만 업데이트 (0원 계좌 등은 스킵)
                if (newBalance != account.getBalance()) {
                    // 잔액과 마지막 업데이트 시각 함께 업데이트
                    accountRepository.updateBalanceAndInterestTime(
                            account.getAccNumber(),
                            newBalance,
                            now
                    );

                    updatedCount++;

                    System.out.println(String.format(
                            "[InterestScheduler] 계좌 %s: %d원 → %d원 (%d시간 경과)",
                            account.getAccNumber(),
                            account.getBalance(),
                            newBalance,
                            hoursElapsed
                    ));
                }

            } catch (Exception e) {
                // 개별 계좌 처리 실패해도 다른 계좌는 계속 처리
                System.err.println("[InterestScheduler] 계좌 " + account.getAccNumber() + " 이자 적용 실패: " + e.getMessage());
            }
        }

        System.out.println("[InterestScheduler] 이자 적용 완료 - 업데이트된 계좌 수: " + updatedCount);
    }
}
