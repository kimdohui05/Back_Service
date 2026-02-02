/**
 * SignupPage.jsx
 *
 * 역할: 회원가입 페이지
 * 기능:
 * - 아이디/비밀번호 입력 폼
 * - 비밀번호 유효성 검사 (8자 이상 + 숫자 포함)
 * - 회원가입 버튼
 *
 * 비밀번호 조건:
 * - 8자 초과
 * - 숫자 포함 필수
 *
 * TODO:
 * - 회원가입 API 연동
 * - 회원가입 성공 시 로그인 페이지로 이동
 */

import React, { useState } from "react";

function SignupPage() {
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();

    const hasNumber = /\d/.test(password);

    if (password.length <= 8 || !hasNumber) {
      setError("비밀번호가 틀렸습니다");
    } else {
      setError("");
      console.log("회원가입 성공");
    }
  };

  return (
    <main className="page-container">
      <div className="form-box">
        <h2>회원가입</h2>

        {error && <p className="error-message">{error}</p>}

        <form className="form-container" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">아이디</label>
            <input id="username" type="text" placeholder="아이디 입력" />
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              placeholder="비밀번호 입력"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="form-btn">
            회원가입
          </button>
        </form>
      </div>
    </main>
  );
}

export default SignupPage;
