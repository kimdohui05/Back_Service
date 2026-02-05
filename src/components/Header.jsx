/**
 * Header.jsx
 *
 * 역할: 모든 페이지 상단에 표시되는 헤더 컴포넌트
 * 기능:
 * - BankService 로고 표시
 * - 로고 클릭 시 메인 페이지(/)로 이동
 * - 로그인 상태에 따라 버튼 표시 변경
 *   - 비로그인: 로그인/회원가입 버튼
 *   - 로그인: 로그아웃 버튼
 * - 화면 상단에 고정 (fixed)
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Header.css";

function Header() {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userId, setUserId] = useState("");

  // 로그인 상태 확인
  useEffect(() => {
    const checkLoginStatus = () => {
      const storedUserId = localStorage.getItem("userId");
      if (storedUserId) {
        setIsLoggedIn(true);
        setUserId(storedUserId);
      } else {
        setIsLoggedIn(false);
        setUserId("");
      }
    };

    // 초기 로그인 상태 확인
    checkLoginStatus();

    // 로그인 상태 변경 이벤트 리스너
    window.addEventListener("loginStatusChanged", checkLoginStatus);

    return () => {
      window.removeEventListener("loginStatusChanged", checkLoginStatus);
    };
  }, []);

  // 로그아웃 처리
  const handleLogout = () => {
    localStorage.removeItem("userId");
    setIsLoggedIn(false);
    setUserId("");
    navigate("/");
  };

  return (
    <header className="App-header">
      <div className="header-left">
        <span className="header-title" onClick={() => navigate("/")} style={{ cursor: "pointer" }}>
          BankService
        </span>
      </div>
      <div className="header-right">
        {isLoggedIn ? (
          // 로그인 상태: 사용자 정보 + 로그아웃 버튼
          <>
            <span className="user-info">{userId}님</span>
            <button className="header-btn" onClick={handleLogout}>
              로그아웃
            </button>
          </>
        ) : (
          // 비로그인 상태: 로그인/회원가입 버튼
          <>
            <button className="header-btn" onClick={() => navigate("/login")}>
              로그인
            </button>
            <button className="header-btn signup-btn" onClick={() => navigate("/signup")}>
              회원가입
            </button>
          </>
        )}
      </div>
    </header>
  );
}

export default Header;
