/**
 * Header.jsx
 *
 * 역할: 모든 페이지 상단에 표시되는 헤더 컴포넌트
 * 기능:
 * - BankService 로고 표시
 * - 로고 클릭 시 메인 페이지(/)로 이동
 * - 화면 상단에 고정 (fixed)
 */

import React from "react";
import { useNavigate } from "react-router-dom";
import "../css/Header.css";

function Header() {
  const navigate = useNavigate();

  return (
    <header className="App-header">
      <div className="header-left">
        <span className="header-title" onClick={() => navigate("/")} style = {{ cursor : "pointer"}}>BankService</span>
      </div>
    </header>
  );
}

export default Header;
