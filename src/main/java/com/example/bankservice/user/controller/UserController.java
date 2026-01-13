package com.example.bankservice.user.controller;

// 필요한 클래스들을 가져옴 (import)
import com.example.bankservice.user.dto.Login;  // 로그인 요청 DTO
import com.example.bankservice.user.dto.User;          // 사용자 정보 DTO
import com.example.bankservice.user.service.UserService;  // 사용자 서비스
import org.springframework.web.bind.annotation.*;  // Spring Web 어노테이션들

/**
 * UserController 클래스
 *
 * 역할: 사용자 관련 API 요청을 받는 "창구 직원"
 *
 * Controller가 하는 일:
 * 1. 클라이언트(프론트엔드)로부터 HTTP 요청을 받음
 * 2. 요청을 Service에게 전달
 * 3. Service로부터 받은 결과를 클라이언트에게 응답
 *
 * 비유: 은행 창구 직원
 * - 손님(클라이언트)의 요청을 받음
 * - 실무팀(Service)에게 일을 시킴
 * - 결과를 손님에게 전달
 */

// @RestController: 이 클래스가 REST API를 처리하는 컨트롤러라는 표시
// - REST API란? HTTP 요청(GET, POST, PUT, DELETE 등)을 받아서 JSON으로 응답하는 API
// - @Controller + @ResponseBody를 합친 것
@RestController

// @RequestMapping: 이 컨트롤러의 기본 URL 경로 설정
// - 모든 메서드의 URL 앞에 "/api/users"가 자동으로 붙음
// - 예: register 메서드는 "/api/users/register"가 됨
@RequestMapping("/api/user")
public class UserController {

    // ===== 필드 =====

    /**
     * userService: 실제 비즈니스 로직을 처리하는 서비스
     * - final: 한 번 설정되면 변경 불가 (불변)
     * - 생성자를 통해 주입받음 (의존성 주입, Dependency Injection)
     */
    private final UserService userService;

    // ===== 생성자 =====

    /**
     * UserController 생성자
     *
     * @param userService - Spring이 자동으로 UserService 객체를 만들어서 넣어줌
     *
     * 의존성 주입(Dependency Injection)이란?
     * - UserController가 UserService를 직접 만들지 않음
     * - Spring이 알아서 만들어서 주입(넣어줌)
     * - 왜? 코드 간의 결합도를 낮추고 테스트하기 쉽게 만들기 위해
     *
     * this.userService = userService 의미:
     * - this.userService: 이 클래스의 필드
     * - userService: 파라미터로 받은 값
     * - Spring이 준 UserService 객체를 이 클래스의 필드에 저장
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===== API 메서드들 =====

    /**
     * 회원가입 API
     *
     * @PostMapping: HTTP POST 요청을 처리
     * - POST는 주로 데이터를 생성할 때 사용
     * - 경로: "/api/users/register"
     *
     * @RequestBody: HTTP 요청의 body(본문)를 Java 객체로 변환
     * - 클라이언트가 보낸 JSON → User 객체로 자동 변환
     * - 예: {"id":"hong", "password":"1234", "name":"홍길동", ...}
     *       → User 객체 생성
     *
     * 동작 흐름:
     * 1. 클라이언트가 POST /api/users/register 요청 + User 정보(JSON)
     * 2. Spring이 JSON을 User 객체로 변환
     * 3. userService.registerUser(user) 호출 → 실제 회원가입 처리
     * 4. "회원가입 성공" 문자열을 클라이언트에게 응답
     *
     * @param user - 회원가입할 사용자 정보
     * @return 성공 메시지
     */
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // Service에게 회원가입 처리를 요청
        userService.registerUser(user);

        // 성공 메시지를 클라이언트에게 반환
        return "회원가입 성공";
    }

    /**
     * 로그인 API
     *
     * @PostMapping: HTTP POST 요청을 처리
     * - 경로: "/api/users/login"
     *
     * @RequestBody: JSON → LoginRequest 객체로 변환
     * - 예: {"id":"hong", "password":"1234"}
     *       → LoginRequest 객체 생성
     *
     * 동작 흐름:
     * 1. 클라이언트가 POST /api/users/login 요청 + 로그인 정보(JSON)
     * 2. Spring이 JSON을 LoginRequest 객체로 변환
     * 3. request.getId(): LoginRequest 객체에서 id를 꺼냄
     * 4. request.getPassword(): LoginRequest 객체에서 password를 꺼냄
     * 5. userService.login(id, password) 호출 → 로그인 검증
     * 6. Service가 반환한 결과(성공/실패 메시지)를 그대로 클라이언트에게 응답
     *
     * @param request - 로그인 요청 정보 (id, password)
     * @return 로그인 결과 메시지 ("로그인 성공" 또는 에러 메시지)
     */
    @PostMapping("/login")
    public String login(@RequestBody Login request) {
        // Service에게 로그인 처리를 요청하고, 그 결과를 바로 반환
        // request.getId(): "hong"
        // request.getPassword(): "1234"
        return userService.login(request.getId(), request.getPassword());
    }

    /**
     * 사용자 조회 API (ID로 조회)
     * GET /api/users/{id}
     *
     * @param id - 조회할 사용자 ID
     * @return 사용자 정보 (uid 포함)
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.findUserById(id);
    }
}