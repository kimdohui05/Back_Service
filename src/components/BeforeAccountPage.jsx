/**
 * BeforeAccountPage.jsx
 *
 * 역할: 계좌가 없을 때 표시되는 컴포넌트
 * 기능:
 * - 계좌 만들기 유도
 * - 계좌 개설 안내
 */

import React from "react";
import "../css/AfterLogin.css";

function BeforeAccountPage({ onCreateAccount }) {
  return (
    <div className="main-container">
      <div className="user-welcome">
        <h1 className="welcome-message">계좌를 개설해주세요 🏦</h1>
        <p className="welcome-subtitle">아직 개설된 계좌가 없습니다.</p>
      </div>

      <div className="service-section">
        <h2>계좌 개설 안내</h2>
        <div className="service-grid">
          <div className="service-card">
            <div className="service-icon">💳</div>
            <h3>일반 계좌</h3>
            <p>입출금, 송금</p>
            <p className="feature-highlight">시간당 1% 자동 이자</p>
          </div>
          <div className="service-card">
            <div className="service-icon">📈</div>
            <h3>적금 계좌</h3>
            <p>일일 납입 적금</p>
            <p className="feature-highlight">최대 1.5% 일일 이자</p>
          </div>
        </div>
      </div>

      <div className="service-section" style={{ textAlign: 'center' }}>
        <button
          className="btn-service"
          onClick={onCreateAccount}
          style={{
            fontSize: '1.2rem',
            padding: '15px 50px',
            backgroundColor: '#5a6268'
          }}
        >
          계좌 개설하기
        </button>
      </div>
    </div>
  );
}

export default BeforeAccountPage;
