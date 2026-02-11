package com.example.bankservice.user.repository;

// 필요한 클래스들을 가져옴
import com.example.bankservice.user.dto.User;  // 사용자 정보 DTO
import org.springframework.jdbc.core.JdbcTemplate;  // Spring의 데이터베이스 작업 도구
import org.springframework.stereotype.Repository;  // Spring Repository 어노테이션
import java.util.UUID;  // 고유 ID 생성 도구

/**
 * UserRepository 클래스
 *
 * 역할: 데이터베이스와 직접 통신하는 "금고 관리자"
 *
 * Repository가 하는 일:
 * 1. Service로부터 데이터 저장/조회 요청을 받음
 * 2. SQL 쿼리를 작성하고 실행
 * 3. 데이터베이스에서 데이터를 저장하거나 가져옴
 * 4. 결과를 Service에게 반환
 *
 * 비유: 은행의 금고 관리자
 * - 실무팀(Service)의 요청을 받음
 * - 금고(데이터베이스)에서 데이터를 꺼내거나 넣음
 * - 결과를 실무팀에게 전달
 */

// @Repository: 이 클래스가 데이터 접근을 담당하는 Repository라는 표시
// - Spring이 이 클래스를 자동으로 관리
// - 데이터베이스 관련 예외를 Spring의 예외로 변환해줌
@Repository
public class UserRepository {

    // ===== 필드 =====

    /**
     * jdbcTemplate: 데이터베이스 작업을 쉽게 해주는 Spring의 도구
     *
     * JdbcTemplate이란?
     * - JDBC (Java Database Connectivity)를 쉽게 사용하게 해주는 도구
     * - SQL 쿼리를 실행하고 결과를 자동으로 Java 객체로 변환
     * - 연결 관리, 예외 처리 등을 자동으로 해줌
     *
     * final: 한 번 설정되면 변경 불가
     */
    private final JdbcTemplate jdbcTemplate;

    // ===== 생성자 =====

    /**
     * UserRepository 생성자
     *
     * @param jdbcTemplate - Spring이 자동으로 만들어서 넣어줌
     *
     * JdbcTemplate은 어디서 오나?
     * - Spring Boot가 application.properties의 DB 설정을 보고
     * - 자동으로 JdbcTemplate 객체를 만들어서 주입해줌
     */
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===== 데이터베이스 작업 메서드들 =====

    /**
     * 사용자를 데이터베이스에 저장하는 메서드
     *
     * 역할: User 객체를 받아서 데이터베이스의 user 테이블에 INSERT
     *
     * 동작 흐름:
     * 1. UUID로 고유한 uid 생성
     * 2. INSERT SQL 쿼리 작성
     * 3. JdbcTemplate으로 쿼리 실행
     *
     * @param user - 저장할 사용자 정보
     */
    public void save(User user) {
        // 1단계: uid (사용자 고유번호) 자동 생성
        // UUID란? Universally Unique Identifier (전 세계에서 유일한 식별자)
        // 예: "550e8400-e29b-41d4-a716-446655440000"
        // randomUUID(): 랜덤으로 UUID 생성
        // toString(): UUID를 문자열로 변환
        String uid = UUID.randomUUID().toString();

        // 2단계: SQL 쿼리 작성
        // INSERT INTO: 데이터베이스에 새 행(row)을 추가하는 명령
        // User: 테이블 이름
        // (uid, id, password, ...): 컬럼 이름들
        // VALUES (?, ?, ?, ...): 실제 값들 (? 는 나중에 채워질 자리)
        String sql = "INSERT INTO User (uid, id, password, name, nickname, phone_number, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // 3단계: JdbcTemplate으로 SQL 실행
        // jdbcTemplate.update(): INSERT, UPDATE, DELETE 쿼리를 실행하는 메서드
        // 첫 번째 파라미터: SQL 쿼리 문자열
        // 나머지 파라미터들: ? 자리에 들어갈 값들 (순서대로)
        //   ? 1번 → uid (자동 생성한 UUID)
        //   ? 2번 → user.getId() (사용자가 입력한 아이디)
        //   ? 3번 → user.getPassword() (사용자가 입력한 비밀번호)
        //   ? 4번 → user.getName() (이름, 최대 4글자)
        //   ? 5번 → user.getNickname() (닉네임, 최대 10글자)
        //   ? 6번 → user.getPhoneNumber() (전화번호)
        //   ? 7번 → user.getEmail() (이메일)
        jdbcTemplate.update(sql, uid, user.getId(), user.getPassword(),
                user.getName(), user.getNickname(), user.getPhoneNumber(), user.getEmail());

        // 실행되는 실제 쿼리 예:
        // INSERT INTO User (uid, id, password, name, nickname, phone_number, email)
        // VALUES ('550e8400-...', 'hong', '1234', '홍길동', '홍길동123', '01012345678', 'hong@example.com')
    }

    /**
     * 아이디로 사용자를 찾는 메서드
     *
     * 역할: 아이디로 데이터베이스에서 사용자를 검색
     *
     * 동작 흐름:
     * 1. SELECT SQL 쿼리 작성
     * 2. JdbcTemplate으로 쿼리 실행
     * 3. 결과를 User 객체로 변환
     * 4. 찾으면 User 반환, 못 찾으면 null 반환
     *
     * @param id - 찾을 사용자의 아이디
     * @return 찾은 User 객체 또는 null
     */
    public User findById(String id) {
        // 1단계: SQL 쿼리 작성
        // SELECT: 데이터베이스에서 데이터를 조회하는 명령
        // uid, id, ...: 가져올 컬럼들
        // FROM User: User 테이블에서
        // WHERE id = ?: id 컬럼이 ? 와 같은 행만 찾기
        String sql = "SELECT uid, id, password, name, nickname, phone_number, email FROM User WHERE id = ?";

        // 2단계: try-catch로 예외 처리
        // 왜? 데이터가 없으면 queryForObject가 예외를 던지기 때문
        try {
            // 3단계: JdbcTemplate으로 쿼리 실행 및 결과를 User 객체로 변환
            // jdbcTemplate.queryForObject(): 결과가 1개인 SELECT 쿼리 실행
            //
            // 파라미터 설명:
            // 1) sql: 실행할 SQL 쿼리
            // 2) (rs, rowNum) -> { ... }: RowMapper (결과를 객체로 변환하는 함수)
            // 3) id: ? 자리에 들어갈 값
            return jdbcTemplate.queryForObject(sql,
                    // RowMapper: 데이터베이스 결과(ResultSet)를 User 객체로 변환
                    // rs: ResultSet (쿼리 결과)
                    // rowNum: 행 번호 (여기서는 사용 안 함)
                    (rs, rowNum) -> User.builder()
                            // rs.getString("uid"): 결과에서 uid 컬럼 값을 가져옴
                            .uid(rs.getString("uid"))
                            // rs.getString("id"): 결과에서 id 컬럼 값을 가져옴
                            .id(rs.getString("id"))
                            .password(rs.getString("password"))
                            .name(rs.getString("name"))
                            .nickname(rs.getString("nickname"))
                            // 주의: DB는 phone_number, Java는 phoneNumber
                            .phoneNumber(rs.getString("phone_number"))
                            .email(rs.getString("email"))
                            // .build(): User 객체 생성 완료
                            .build(),
                    // WHERE id = ? 의 ? 에 들어갈 값
                    id
            );

            // 실행되는 실제 쿼리 예:
            // SELECT uid, id, password, name, nickname, phone_number, email
            // FROM User
            // WHERE id = 'hong'

        } catch (Exception e) {
            // 4단계: 예외 발생 시 (데이터가 없거나 에러 발생)
            // null을 반환
            // Service에서 null 체크를 통해 "아이디가 존재하지 않습니다" 메시지를 보냄
            return null;
        }
    }
}