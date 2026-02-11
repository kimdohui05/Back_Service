/**
 * BeforeLoginPage.jsx
 *
 * 역할: 로그인 전 메인 페이지 컴포넌트
 * 기능:
 * - 주요 서비스 소개 (일반 계좌, 적금 계좌, 안전한 보안)
 */

import React from "react";
import "../css/BeforeLogin.css";

function BeforeLoginPage() {
  return (
    <div className="main-container">
      <div className="features-section">
        <h2>주요 서비스</h2>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">💰</div>
            <h3>일반 계좌</h3>
            <p>입출금, 송금</p>
            <p className="feature-highlight">시간당 1% 자동 이자</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📈</div>
            <h3>적금 계좌</h3>
            <p>일일 납입 적금</p>
            <p className="feature-highlight">최대 1.5% 일일 이자</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🔒</div>
            <h3>안전한 보안</h3>
            <p>계좌 비밀번호</p>
            <p className="feature-highlight">안전한 거래</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default BeforeLoginPage;
