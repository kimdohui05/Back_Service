package com.example.bankservice.savings.repository;

import com.example.bankservice.savings.dto.Savings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * SavingsRepository 클래스
 *
 * 역할: 적금 계좌 데이터베이스와 직접 통신
 * - 적금 계좌 저장, 조회, 잔액 업데이트 등
 */
@Repository
public class SavingsRepository {

    private final JdbcTemplate jdbcTemplate;

    public SavingsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 새 적금 계좌를 데이터베이스에 저장
     *
     * @param savings - 저장할 적금 계좌 정보
     */
    public void save(Savings savings) {
        // sid (적금 계좌 고유번호) 자동 생성
        String sid = UUID.randomUUID().toString();

        String sql = "INSERT INTO savings (sid, uid, acc_number, acc_password, rate, current_rate, start_date, status, balance, principal, period, mthly_deposit, last_deposit_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                sid,
                savings.getUid(),
                savings.getAccNumber(),
                savings.getAccPassword(),
                savings.getRate(),
                savings.getCurrentRate(),
                savings.getStartDate(),
                savings.getStatus(),
                savings.getBalance(),
                savings.getPrincipal(),
                savings.getPeriod(),
                savings.getDailyDeposit(),
                savings.getLastDepositDate()
        );
    }

    /**
     * 적금 계좌번호로 적금 계좌 찾기
     *
     * @param accNumber - 적금 계좌번호
     * @return 찾은 적금 계좌 또는 null
     */
    public Savings findByAccNumber(String accNumber) {
        String sql = "SELECT sid, uid, acc_number, acc_password, rate, current_rate, start_date, status, balance, principal, period, mthly_deposit, last_deposit_date " +
                "FROM savings WHERE acc_number = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> Savings.builder()
                            .sid(rs.getString("sid"))
                            .uid(rs.getString("uid"))
                            .accNumber(rs.getString("acc_number"))
                            .accPassword(rs.getString("acc_password"))
                            .rate(rs.getDouble("rate"))
                            .currentRate(rs.getDouble("current_rate"))
                            .startDate(rs.getDate("start_date").toLocalDate())
                            .status(rs.getString("status"))
                            .balance(rs.getLong("balance"))
                            .principal(rs.getLong("principal"))
                            .period(rs.getInt("period"))
                            .dailyDeposit(rs.getInt("mthly_deposit"))
                            .lastDepositDate(rs.getDate("last_deposit_date") != null ?
                                    rs.getDate("last_deposit_date").toLocalDate() : null)
                            .build(),
                    accNumber
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 유저 ID로 해당 유저의 모든 적금 계좌 조회
     *
     * @param uid - 유저 고유번호
     * @return 적금 계좌 목록
     */
    public List<Savings> findByUid(String uid) {
        String sql = "SELECT sid, uid, acc_number, acc_password, rate, current_rate, start_date, status, balance, principal, period, mthly_deposit, last_deposit_date " +
                "FROM savings WHERE uid = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Savings.builder()
                        .sid(rs.getString("sid"))
                        .uid(rs.getString("uid"))
                        .accNumber(rs.getString("acc_number"))
                        .accPassword(rs.getString("acc_password"))
                        .rate(rs.getDouble("rate"))
                        .currentRate(rs.getDouble("current_rate"))
                        .startDate(rs.getDate("start_date").toLocalDate())
                        .status(rs.getString("status"))
                        .balance(rs.getLong("balance"))
                        .principal(rs.getLong("principal"))
                        .period(rs.getInt("period"))
                        .dailyDeposit(rs.getInt("mthly_deposit"))
                        .lastDepositDate(rs.getDate("last_deposit_date") != null ?
                                rs.getDate("last_deposit_date").toLocalDate() : null)
                        .build(),
                uid
        );
    }

    /**
     * 적금 계좌 잔액 업데이트
     *
     * @param accNumber - 적금 계좌번호
     * @param newBalance - 새로운 잔액
     */
    public void updateBalance(String accNumber, Long newBalance) {
        String sql = "UPDATE savings SET balance = ? WHERE acc_number = ?";
        jdbcTemplate.update(sql, newBalance, accNumber);
    }

    /**
     * 적금 계좌 상태 업데이트
     *
     * @param accNumber - 적금 계좌번호
     * @param status - 새로운 상태 (ACTIVE, MATURED, CLOSED)
     */
    public void updateStatus(String accNumber, String status) {
        String sql = "UPDATE savings SET status = ? WHERE acc_number = ?";
        jdbcTemplate.update(sql, status, accNumber);
    }

    /**
     * 12자리 랜덤 적금 계좌번호 생성
     * - 맨 앞자리는 9로 시작
     * - 나머지 11자리는 랜덤 숫자
     * - 중복 방지: 이미 존재하는 계좌번호면 다시 생성
     *
     * @return 12자리 적금 계좌번호 (9로 시작)
     */
    public String generateSavingsAccountNumber() {
        Random random = new Random();
        String accNumber;
        int maxAttempts = 100; // 최대 시도 횟수
        int attempts = 0;

        do {
            StringBuilder sb = new StringBuilder();

            // 첫 번째 자리는 9로 고정 (적금 계좌)
            sb.append("9");

            // 나머지 11자리 숫자 생성
            for (int i = 0; i < 11; i++) {
                sb.append(random.nextInt(10)); // 0~9 랜덤 숫자
            }

            accNumber = sb.toString();
            attempts++;

            // 무한 루프 방지
            if (attempts >= maxAttempts) {
                throw new RuntimeException("적금 계좌번호 생성 실패: 최대 시도 횟수 초과");
            }

        } while (findByAccNumber(accNumber) != null); // 중복이면 다시 생성

        return accNumber;
    }

    /**
     * 모든 적금 계좌 조회
     *
     * @return 전체 적금 계좌 목록
     */
    public List<Savings> findAll() {
        String sql = "SELECT sid, uid, acc_number, acc_password, rate, current_rate, start_date, status, balance, principal, period, mthly_deposit, last_deposit_date " +
                "FROM savings";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Savings.builder()
                        .sid(rs.getString("sid"))
                        .uid(rs.getString("uid"))
                        .accNumber(rs.getString("acc_number"))
                        .accPassword(rs.getString("acc_password"))
                        .rate(rs.getDouble("rate"))
                        .currentRate(rs.getDouble("current_rate"))
                        .startDate(rs.getDate("start_date").toLocalDate())
                        .status(rs.getString("status"))
                        .balance(rs.getLong("balance"))
                        .principal(rs.getLong("principal"))
                        .period(rs.getInt("period"))
                        .dailyDeposit(rs.getInt("mthly_deposit"))
                        .lastDepositDate(rs.getDate("last_deposit_date") != null ?
                                rs.getDate("last_deposit_date").toLocalDate() : null)
                        .build()
        );
    }

    /**
     * 적금 계좌 잔액, 원금, 마지막 입금 날짜 함께 업데이트
     * (일일 납입액 이상 입금 시 사용)
     *
     * @param accNumber - 적금 계좌번호
     * @param newBalance - 새로운 잔액
     * @param newPrincipal - 새로운 원금
     * @param depositDate - 입금 날짜
     */
    public void updateBalanceAndPrincipalAndDepositDate(String accNumber, Long newBalance, Long newPrincipal, java.time.LocalDate depositDate) {
        String sql = "UPDATE savings SET balance = ?, principal = ?, last_deposit_date = ? WHERE acc_number = ?";
        jdbcTemplate.update(sql, newBalance, newPrincipal, depositDate, accNumber);
    }

    /**
     * 적금 계좌 잔액과 원금만 업데이트 (마지막 입금 날짜는 업데이트 안 함)
     * (일일 납입액 미만 입금 시 사용 - "입금 안 했다"고 판단)
     *
     * @param accNumber - 적금 계좌번호
     * @param newBalance - 새로운 잔액
     * @param newPrincipal - 새로운 원금
     */
    public void updateBalanceAndPrincipal(String accNumber, Long newBalance, Long newPrincipal) {
        String sql = "UPDATE savings SET balance = ?, principal = ? WHERE acc_number = ?";
        jdbcTemplate.update(sql, newBalance, newPrincipal, accNumber);
    }

    /**
     * ACTIVE 상태이면서 어제 입금한 적금 계좌 목록 조회
     * (자정에 이자를 적용하기 위함)
     *
     * @param yesterday - 어제 날짜
     * @return 이자를 적용할 적금 계좌 목록
     */
    public List<Savings> findActiveWithDepositOnDate(java.time.LocalDate yesterday) {
        String sql = "SELECT sid, uid, acc_number, acc_password, rate, current_rate, start_date, status, balance, principal, period, mthly_deposit, last_deposit_date " +
                "FROM savings WHERE status = 'ACTIVE' AND last_deposit_date = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Savings.builder()
                        .sid(rs.getString("sid"))
                        .uid(rs.getString("uid"))
                        .accNumber(rs.getString("acc_number"))
                        .accPassword(rs.getString("acc_password"))
                        .rate(rs.getDouble("rate"))
                        .currentRate(rs.getDouble("current_rate"))
                        .startDate(rs.getDate("start_date").toLocalDate())
                        .status(rs.getString("status"))
                        .balance(rs.getLong("balance"))
                        .principal(rs.getLong("principal"))
                        .period(rs.getInt("period"))
                        .dailyDeposit(rs.getInt("mthly_deposit"))
                        .lastDepositDate(rs.getDate("last_deposit_date").toLocalDate())
                        .build(),
                yesterday
        );
    }

    /**
     * ACTIVE 상태이면서 어제 입금하지 않은 적금 계좌 목록 조회
     * (이자율 감소 적용을 위함)
     *
     * @param yesterday - 어제 날짜
     * @return 이자율을 감소시킬 적금 계좌 목록
     */
    public List<Savings> findActiveWithoutDepositOnDate(java.time.LocalDate yesterday) {
        String sql = "SELECT sid, uid, acc_number, acc_password, rate, current_rate, start_date, status, balance, principal, period, mthly_deposit, last_deposit_date " +
                "FROM savings WHERE status = 'ACTIVE' AND (last_deposit_date IS NULL OR last_deposit_date != ?)";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Savings.builder()
                        .sid(rs.getString("sid"))
                        .uid(rs.getString("uid"))
                        .accNumber(rs.getString("acc_number"))
                        .accPassword(rs.getString("acc_password"))
                        .rate(rs.getDouble("rate"))
                        .currentRate(rs.getDouble("current_rate"))
                        .startDate(rs.getDate("start_date").toLocalDate())
                        .status(rs.getString("status"))
                        .balance(rs.getLong("balance"))
                        .principal(rs.getLong("principal"))
                        .period(rs.getInt("period"))
                        .dailyDeposit(rs.getInt("mthly_deposit"))
                        .lastDepositDate(rs.getDate("last_deposit_date") != null ?
                                rs.getDate("last_deposit_date").toLocalDate() : null)
                        .build(),
                yesterday
        );
    }

    /**
     * 현재 이자율 업데이트
     *
     * @param accNumber - 적금 계좌번호
     * @param newCurrentRate - 새로운 현재 이자율
     */
    public void updateCurrentRate(String accNumber, Double newCurrentRate) {
        String sql = "UPDATE savings SET current_rate = ? WHERE acc_number = ?";
        jdbcTemplate.update(sql, newCurrentRate, accNumber);
    }
}
