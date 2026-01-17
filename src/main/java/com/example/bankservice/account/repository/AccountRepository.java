package com.example.bankservice.account.repository;

import com.example.bankservice.account.dto.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        String sql = "SELECT aid, uid, acc_number, acc_password, balance " +
                "FROM account WHERE acc_number = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> Account.builder()
                            .aid(rs.getString("aid"))
                            .uid(rs.getString("uid"))
                            .accNumber(rs.getString("acc_number"))
                            .accPassword(rs.getString("acc_password"))
                            .balance(rs.getLong("balance"))
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
        String sql = "SELECT aid, uid, acc_number, acc_password, balance " +
                "FROM account WHERE uid = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Account.builder()
                        .aid(rs.getString("aid"))
                        .uid(rs.getString("uid"))
                        .accNumber(rs.getString("acc_number"))
                        .accPassword(rs.getString("acc_password"))
                        .balance(rs.getLong("balance"))
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
     * 12자리 랜덤 계좌번호 생성
     *
     * @return 12자리 계좌번호
     */
    public String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accNumber = new StringBuilder();

        // 12자리 숫자 생성
        for (int i = 0; i < 12; i++) {
            accNumber.append(random.nextInt(10)); // 0~9 랜덤 숫자
        }

        return accNumber.toString();
    }
}
