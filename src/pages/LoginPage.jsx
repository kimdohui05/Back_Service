/**
 * LoginPage.jsx
 *
 * 역할: 로그인 페이지
 * 기능:
 * - 아이디/비밀번호 입력 폼
 * - 로그인 버튼
 * - 백엔드 API 연동 (POST /api/user/login)
 * - 로그인 성공 시 메인 페이지로 이동
 */

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/LoginPage.css";

function LoginPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    id: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // 입력값 유효성 검사
    if (!formData.id || !formData.password) {
      setError("아이디와 비밀번호를 입력해주세요.");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/user/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      const data = await response.text();

      if (response.ok) {
        // 로그인 성공
        // 사용자 정보 저장
        localStorage.setItem("userId", formData.id);
        // 로그인 상태 변경 이벤트 발생
        window.dispatchEvent(new Event("loginStatusChanged"));
        navigate("/");
      } else {
        // 로그인 실패
        setError(data || "로그인에 실패했습니다.");
      }
    } catch (err) {
      setError("서버와의 연결에 실패했습니다. 백엔드 서버가 실행 중인지 확인해주세요.");
      console.error("Login error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="page-container">
      <div className="form-box">
        <h2>로그인</h2>

        {error && <p className="error-message">{error}</p>}

        <form className="form-container" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="id">아이디</label>
            <input
              id="id"
              name="id"
              type="text"
              placeholder="아이디 입력"
              value={formData.id}
              onChange={handleChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              name="password"
              type="password"
              placeholder="비밀번호 입력"
              value={formData.password}
              onChange={handleChange}
            />
          </div>
          <button type="submit" className="form-btn" disabled={loading}>
            {loading ? "로그인 중..." : "로그인"}
          </button>
        </form>
      </div>
    </main>
  );
}

export default LoginPage;
