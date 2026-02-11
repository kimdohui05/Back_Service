package com.example.bankservice.user.dto;

// Lombok 라이브러리에서 제공하는 기능들을 가져옴 (코드를 자동으로 만들어주는 도구)
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * User DTO (Data Transfer Object)
 *
 * DTO란?
 * - 데이터를 담아서 전달하는 상자 같은 것
 * - 데이터베이스 ↔ Java 코드 사이에서 데이터를 옮길 때 사용
 *
 * 역할: 사용자 정보를 담는 클래스
 * - 회원가입할 때 사용자 정보를 담음
 * - 로그인할 때 사용자 정보를 담음
 * - 데이터베이스의 user 테이블과 1:1 매칭됨
 */

// @Data: Lombok이 자동으로 만들어주는 것들
// - getter: user.getId() 처럼 값을 가져오는 메서드
// - setter: user.setId("hong") 처럼 값을 설정하는 메서드
// - toString: user 정보를 문자열로 출력
// - equals, hashCode: 두 User 객체가 같은지 비교
@Data

// @NoArgsConstructor: 파라미터가 없는 기본 생성자를 자동으로 만들어줌
// 예: User user = new User();
@NoArgsConstructor

// @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자를 자동으로 만들어줌
// 예: User user = new User(uid, id, password, name, phoneNumber, email);
@AllArgsConstructor

// @Builder: 빌더 패턴을 사용할 수 있게 해줌
// 빌더 패턴이란? 객체를 만들 때 보기 좋게 만드는 방법
// 예: User user = User.builder()
//                     .id("hong")
//                     .password("1234")
//                     .name("홍길동")
//                     .build();
@Builder
public class User {

    // ===== 필드 (Field) =====
    // 이 클래스가 가지고 있는 데이터들
    // 데이터베이스의 user 테이블 컬럼과 1:1 매칭됨

    /**
     * uid: 사용자 고유 번호
     * - 데이터베이스에서 자동으로 생성됨 (UUID 사용)
     * - Primary Key (주키): 이 값으로 사용자를 구분함
     * - 예: "550e8400-e29b-41d4-a716-446655440000"
     * - DB 컬럼: uid (VARCHAR(36), PK)
     */
    private String uid;

    /**
     * id: 로그인할 때 사용하는 아이디
     * - 사용자가 직접 입력하는 값
     * - 예: "hong", "kim123"
     * - DB 컬럼: id (VARCHAR(15), NN)
     */
    private String id;

    /**
     * password: 로그인할 때 사용하는 비밀번호
     * - 사용자가 직접 입력하는 값
     * - 예: "1234", "mypassword"
     * - 주의: 실제로는 암호화해서 저장해야 함 (현재는 평문)
     * - DB 컬럼: password (VARCHAR(15), NN)
     */
    private String password;

    /**
     * name: 사용자의 이름
     * - 예: "홍길동", "김철수"
     * - 회원가입 시 사용자가 입력
     * - 최대 길이: 4글자
     * - DB 컬럼: name (VARCHAR(4), NN)
     */
    private String name;

    /**
     * nickname: 사용자의 닉네임
     * - 예: "홍길동123", "멋진사람"
     * - 회원가입 시 사용자가 입력
     * - 최대 길이: 10글자
     * - DB 컬럼: nickname (VARCHAR(10), NN)
     */
    private String nickname;

    /**
     * phoneNumber: 전화번호
     * - 예: "01012345678"
     * - 주의: DB에서는 phone_number (언더바), Java에서는 phoneNumber (카멜케이스)
     * - Spring이 자동으로 변환해줌
     * - DB 컬럼: phone_number (VARCHAR(11), NN)
     */
    private String phoneNumber;

    /**
     * email: 이메일 주소
     * - 예: "hong@example.com"
     * - DB 컬럼: email (VARCHAR(45), NN)
     */
    private String email;
}
