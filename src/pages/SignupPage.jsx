/**
 * SignupPage.jsx
 *
 * 역할: 회원가입 페이지
 * 기능:
 * - 회원가입 폼 (아이디, 비밀번호, 이름, 전화번호, 이메일)
 * - 비밀번호 유효성 검사 (8자 이상 + 숫자 포함)
 * - 백엔드 API 연동 (POST /api/user/register)
 * - 회원가입 성공 시 로그인 페이지로 이동
 *
 * 비밀번호 조건:
 * - 8자 초과
 * - 숫자 포함 필수
 */

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/LoginPage.css";

function SignupPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    id: "",
    password: "",
    name: "",
    phoneNumber: "",
    email: "",
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
    if (!formData.id || !formData.password || !formData.name || !formData.phoneNumber || !formData.email) {
      setError("모든 필드를 입력해주세요.");
      return;
    }

    // 비밀번호 유효성 검사
    const hasNumber = /\d/.test(formData.password);
    if (formData.password.length <= 8 || !hasNumber) {
      setError("비밀번호는 8자 초과, 숫자 포함 필수입니다.");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/user/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      const data = await response.text();

      if (response.ok) {
        // 회원가입 성공
        alert(data);
        navigate("/login");
      } else {
        // 회원가입 실패
        setError(data || "회원가입에 실패했습니다.");
      }
    } catch (err) {
      setError("서버와의 연결에 실패했습니다. 백엔드 서버가 실행 중인지 확인해주세요.");
      console.error("Signup error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="page-container">
      <div className="form-box">
        <h2>회원가입</h2>

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
              placeholder="비밀번호 입력 (8자 초과, 숫자 포함)"
              value={formData.password}
              onChange={handleChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="name">이름</label>
            <input
              id="name"
              name="name"
              type="text"
              placeholder="이름 입력"
              value={formData.name}
              onChange={handleChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="phoneNumber">전화번호</label>
            <input
              id="phoneNumber"
              name="phoneNumber"
              type="text"
              placeholder="전화번호 입력 (예: 01012345678)"
              value={formData.phoneNumber}
              onChange={handleChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">이메일</label>
            <input
              id="email"
              name="email"
              type="email"
              placeholder="이메일 입력"
              value={formData.email}
              onChange={handleChange}
            />
          </div>
          <button type="submit" className="form-btn" disabled={loading}>
            {loading ? "가입 중..." : "회원가입"}
          </button>
        </form>
      </div>
    </main>
  );
}

export default SignupPage;
