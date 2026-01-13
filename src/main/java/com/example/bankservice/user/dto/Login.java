package com.example.bankservice.user.dto;

// Lombok 라이브러리 import
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Login DTO (LoginRequest 역할)
 *
 * 역할: 로그인할 때 필요한 정보만 담는 클래스
 *
 * 왜 User 클래스를 안 쓰고 Login을 따로 만들었나?
 * - 로그인할 때는 id와 password만 필요함
 * - User 클래스는 name, phoneNumber, email 등 불필요한 정보도 많음
 * - 딱 필요한 정보만 받기 위해 별도의 DTO를 만듦
 *
 * 사용 예:
 * - 사용자가 로그인 버튼 클릭
 * - 프론트엔드가 { "id": "hong", "password": "1234" } JSON 전송
 * - Spring이 자동으로 Login 객체로 변환
 */

// @Data: getter, setter, toString 등을 자동으로 만들어줌
@Data

// @NoArgsConstructor: 기본 생성자를 자동으로 만들어줌
// 예: Login login = new Login();
@NoArgsConstructor

// @AllArgsConstructor: 모든 필드를 받는 생성자를 자동으로 만들어줌
// 예: Login login = new Login("hong", "1234");
@AllArgsConstructor
public class Login {

    /**
     * id: 로그인 아이디
     * - 사용자가 입력한 아이디
     * - 예: "hong", "kim123"
     */
    private String id;

    /**
     * password: 로그인 비밀번호
     * - 사용자가 입력한 비밀번호
     * - 예: "1234", "mypassword"
     */
    private String password;
}