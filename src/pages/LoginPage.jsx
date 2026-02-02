/**
 * LoginPage.jsx
 *
 * 역할: 로그인 페이지
 * 기능:
 * - 아이디/비밀번호 입력 폼
 * - 로그인 버튼
 *
 * TODO:
 * - 로그인 API 연동
 * - 입력값 유효성 검사
 * - 로그인 성공 시 메인 페이지로 이동
 */

import "../pages/LoginPage";

function LoginPage() {
  return (
    <main className="page-container">
      <div className="form-box">
        <h2>로그인</h2>
        <form className="form-container">
          <div className="form-group">
            <label htmlFor="username">아이디</label>
            <input id="username" type="text" placeholder="아이디 입력" />
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input id="password" type="password" placeholder="비밀번호 입력" />
          </div>
          <button type="submit" className="form-btn">로그인</button>
        </form>
      </div>
    </main>
  );
}

export default LoginPage;
