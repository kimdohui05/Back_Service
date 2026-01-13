package com.example.bankservice.user.service;

// 필요한 클래스들을 가져옴
import com.example.bankservice.user.dto.User;  // 사용자 정보 DTO
import com.example.bankservice.user.repository.UserRepository;  // 사용자 Repository
import org.springframework.stereotype.Service;  // Spring Service 어노테이션

/**
 * UserService 클래스
 *
 * 역할: 사용자 관련 비즈니스 로직을 처리하는 "실무 담당자"
 *
 * Service가 하는 일:
 * 1. Controller로부터 요청을 받음
 * 2. 실제 비즈니스 로직을 수행 (검증, 계산, 처리 등)
 * 3. Repository에게 데이터 저장/조회 요청
 * 4. 결과를 Controller에게 반환
 *
 * 비유: 은행의 실무 담당 직원
 * - 창구 직원(Controller)으로부터 일을 받음
 * - 실제로 업무를 처리 (아이디 확인, 비밀번호 검증 등)
 * - 금고 관리자(Repository)에게 데이터 저장/조회 시킨 후
 * - 결과를 창구 직원에게 전달
 */

// @Service: 이 클래스가 비즈니스 로직을 처리하는 서비스라는 표시
// - Spring이 이 클래스를 자동으로 관리하고, 필요한 곳에 주입해줌
// - Spring Container에 Bean으로 등록됨
@Service
public class UserService {

    // ===== 필드 =====

    /**
     * userRepository: 데이터베이스와 직접 통신하는 Repository
     * - final: 한 번 설정되면 변경 불가
     * - 생성자를 통해 주입받음 (의존성 주입)
     */
    private final UserRepository userRepository;

    // ===== 생성자 =====

    /**
     * UserService 생성자
     *
     * @param userRepository - Spring이 자동으로 UserRepository 객체를 만들어서 넣어줌
     *
     * 의존성 주입(Dependency Injection):
     * - UserService가 UserRepository를 직접 만들지 않음
     * - Spring이 알아서 만들어서 주입(넣어줌)
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===== 비즈니스 로직 메서드들 =====

    /**
     * 회원가입 처리 메서드
     *
     * 역할: 새로운 사용자를 데이터베이스에 저장
     *
     * 동작 흐름:
     * 1. Controller가 User 객체를 넘겨줌
     * 2. Repository에게 User를 저장하라고 요청
     * 3. Repository가 데이터베이스에 INSERT 쿼리 실행
     *
     * 현재는 단순하지만, 나중에 추가할 수 있는 것들:
     * - 아이디 중복 체크
     * - 비밀번호 암호화
     * - 이메일 유효성 검증
     * - 환영 이메일 발송 등
     *
     * @param user - 회원가입할 사용자 정보
     */
    public void registerUser(User user) {
        // Repository에게 사용자 정보를 데이터베이스에 저장하라고 요청
        userRepository.save(user);

        // 반환값 없음 (void)
        // 저장만 하면 끝
    }

    /**
     * 로그인 처리 메서드
     *
     * 역할: 사용자의 아이디와 비밀번호를 확인하여 로그인 성공/실패 판단
     *
     * 동작 흐름:
     * 1. Repository에게 아이디로 사용자를 찾아달라고 요청
     * 2. 사용자가 없으면 → "아이디가 존재하지 않습니다" 반환
     * 3. 사용자가 있으면 → 비밀번호 비교
     * 4. 비밀번호가 다르면 → "비밀번호가 틀렸습니다" 반환
     * 5. 비밀번호가 같으면 → "로그인 성공" 반환
     *
     * @param id - 로그인 시도하는 아이디
     * @param password - 로그인 시도하는 비밀번호
     * @return 로그인 결과 메시지
     */
    public String login(String id, String password) {
        // 1단계: Repository에게 아이디로 사용자 찾기 요청
        // findById(id)는 데이터베이스에서 id가 일치하는 사용자를 찾음
        // 찾으면 User 객체 반환, 못 찾으면 null 반환
        User user = userRepository.findById(id);

        // 2단계: 아이디가 존재하는지 확인
        // user == null: 데이터베이스에 해당 아이디가 없음
        if (user == null) {
            return "아이디가 존재하지 않습니다";
        }

        // 3단계: 비밀번호가 맞는지 확인
        // user.getPassword(): 데이터베이스에 저장된 비밀번호
        // password: 사용자가 입력한 비밀번호
        // equals(): 두 문자열이 같은지 비교
        // !: NOT 연산자, 같지 않으면 true
        if (!user.getPassword().equals(password)) {
            return "비밀번호가 틀렸습니다";
        }

        // 4단계: 모든 검증 통과 → 로그인 성공
        // 여기까지 왔다는 것은:
        // - 아이디가 존재하고 (user != null)
        // - 비밀번호도 일치한다는 뜻
        return "로그인 성공";
    }

    /**
     * 사용자 조회 메서드
     *
     * @param id - 조회할 사용자 아이디
     * @return User 객체
     */
    public User findUserById(String id) {
        return userRepository.findById(id);
    }
}