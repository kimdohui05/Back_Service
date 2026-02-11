package com.example.bankservice.account.repository;

import com.example.bankservice.account.dto.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * AccountRepository 클래스
 *
 * 역할: 계좌 데이터베이스와 직접 통신
 * - 계좌 저장, 조회, 잔액 업데이트 등
 */
@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 새 계좌를 데이터베이스에 저장
     *
     * @param account - 저장할 계좌 정보
     */
    public void save(Account account) {
        // aid (계좌 고유번호) 자동 생성
        String aid = UUID.randomUUID().toString();

        // last_interest_update는 DB의 DEFAULT CURRENT_TIMESTAMP로 자동 설정됨
        String sql = "INSERT INTO account (aid, uid, acc_number, acc_password, balance) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                aid,
                account.getUid(),
                account.getAccNumber(),
                account.getAccPassword(),
                account.getBalance()
        );
    }

    /**
     * 계좌번호로 계좌 찾기
     *
     * @param accNumber - 계좌번호
     * @return 찾은 계좌 또는 null
     */
    public Account findByAccNumber(String accNumber) {
        String sql = "SELECT aid, uid, acc_number, acc_password, balance, last_interest_update " +
                "FROM account WHERE acc_number = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> Account.builder()
                            .aid(rs.getString("aid"))
                            .uid(rs.getString("uid"))
                            .accNumber(rs.getString("acc_number"))
                            .accPassword(rs.getString("acc_password"))
                            .balance(rs.getLong("balance"))
                            .lastInterestUpdate(rs.getTimestamp("last_interest_update") != null
                                    ? rs.getTimestamp("last_interest_update").toLocalDateTime()
                                    : null)
                            .build(),
                    accNumber
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 유저 ID로 해당 유저의 모든 계좌 조회
     *
     * @param uid - 유저 고유번호
     * @return 계좌 목록
     */
    public List<Account> findByUid(String uid) {
        String sql = "SELECT aid, uid, acc_number, acc_password, balance, last_interest_update " +
                "FROM account WHERE uid = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Account.builder()
                        .aid(rs.getString("aid"))
                        .uid(rs.getString("uid"))
                        .accNumber(rs.getString("acc_number"))
                        .accPassword(rs.getString("acc_password"))
                        .balance(rs.getLong("balance"))
                        .lastInterestUpdate(rs.getTimestamp("last_interest_update") != null
                                ? rs.getTimestamp("last_interest_update").toLocalDateTime()
                                : null)
                        .build(),
                uid
        );
    }

    /**
     * 계좌 잔액 업데이트
     *
     * @param accNumber - 계좌번호
     * @param newBalance - 새로운 잔액
     */
    public void updateBalance(String accNumber, Long newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE acc_number = ?";
        jdbcTemplate.update(sql, newBalance, accNumber);
    }

    /**
     * 12자리 랜덤 계좌번호 생성 (일반 계좌용)
     * - 맨 앞자리는 0으로 시작
     * - 나머지 11자리는 랜덤 숫자
     * - 중복 방지: 이미 존재하는 계좌번호면 다시 생성
     *
     * @return 12자리 계좌번호 (0으로 시작)
     */
    public String generateAccountNumber() {
        Random random = new Random();
        String accNumber;
        int maxAttempts = 100; // 최대 시도 횟수
        int attempts = 0;

        do {
            StringBuilder sb = new StringBuilder();

            // 첫 번째 자리는 0으로 고정
            sb.append("0");

            // 나머지 11자리 숫자 생성
            for (int i = 0; i < 11; i++) {
                sb.append(random.nextInt(10)); // 0~9 랜덤 숫자
            }

            accNumber = sb.toString();
            attempts++;

            // 무한 루프 방지
            if (attempts >= maxAttempts) {
                throw new RuntimeException("계좌번호 생성 실패: 최대 시도 횟수 초과");
            }

        } while (findByAccNumber(accNumber) != null); // 중복이면 다시 생성

        return accNumber;
    }

    /**
     * 모든 계좌 조회
     *
     * @return 전체 계좌 목록
     *
     * 용도: 이자 스케줄러에서 사용
     * - 1시간마다 모든 계좌에 이자를 적용하기 위해 전체 계좌 조회 필요
     */
    public List<Account> findAll() {
        String sql = "SELECT aid, uid, acc_number, acc_password, balance, last_interest_update " +
                "FROM account";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Account.builder()
                        .aid(rs.getString("aid"))
                        .uid(rs.getString("uid"))
                        .accNumber(rs.getString("acc_number"))
                        .accPassword(rs.getString("acc_password"))
                        .balance(rs.getLong("balance"))
                        .lastInterestUpdate(rs.getTimestamp("last_interest_update") != null
                                ? rs.getTimestamp("last_interest_update").toLocalDateTime()
                                : null)
                        .build()
        );
    }

    /**
     * 계좌 잔액과 마지막 이자 업데이트 시각 함께 업데이트
     *
     * @param accNumber - 계좌번호
     * @param newBalance - 새로운 잔액
     * @param updateTime - 업데이트 시각
     *
     * 용도: 이자 스케줄러에서 사용
     * - 이자 적용 후 잔액과 적용 시각을 함께 업데이트
     */
    public void updateBalanceAndInterestTime(String accNumber, Long newBalance, LocalDateTime updateTime) {
        String sql = "UPDATE account SET balance = ?, last_interest_update = ? WHERE acc_number = ?";
        jdbcTemplate.update(sql, newBalance, updateTime, accNumber);
    }
}
