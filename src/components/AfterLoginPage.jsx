/**
 * AfterLoginPage.jsx
 *
 * 역할: 로그인 후 메인 페이지 컴포넌트
 * 기능:
 * - 계좌 존재 여부 확인
 * - 계좌 없음: BeforeAccountPage 표시
 * - 계좌 있음: 계좌 관리 메뉴 표시
 */

import React, { useState, useEffect } from "react";
import BeforeAccountPage from "./BeforeAccountPage";
import "../css/AfterLogin.css";

function AfterLoginPage({ userId }) {
  const [hasAccount, setHasAccount] = useState(null); // null: 로딩 중, true: 계좌 있음, false: 계좌 없음
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);

  // 계좌 목록 조회
  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        // userId로 uid 조회 (실제로는 localStorage에 uid를 저장하는 것이 좋음)
        const userResponse = await fetch(`http://localhost:8080/api/user/${userId}`);
        if (!userResponse.ok) {
          console.error("사용자 정보 조회 실패");
          setHasAccount(false);
          setLoading(false);
          return;
        }

        const userData = await userResponse.json();
        const uid = userData.uid;

        // 계좌 목록 조회
        const accountResponse = await fetch(`http://localhost:8080/api/account/my/${uid}`);

        if (accountResponse.ok) {
          const accountData = await accountResponse.json();
          setAccounts(accountData);
          setHasAccount(accountData.length > 0);
        } else {
          setHasAccount(false);
        }
      } catch (error) {
        console.error("계좌 조회 에러:", error);
        setHasAccount(false);
      } finally {
        setLoading(false);
      }
    };

    fetchAccounts();
  }, [userId]);

  // 계좌 개설 버튼 클릭
  const handleCreateAccount = () => {
    alert("계좌 개설 기능은 추후 구현 예정입니다.");
    // TODO: 계좌 개설 페이지로 이동 또는 모달 표시
  };

  // 로딩 중
  if (loading) {
    return (
      <div className="main-container">
        <div className="user-welcome">
          <h1 className="welcome-message">로딩 중...</h1>
        </div>
      </div>
    );
  }

  // 계좌 없음
  if (!hasAccount) {
    return <BeforeAccountPage onCreateAccount={handleCreateAccount} />;
  }

  // 계좌 있음 - 기존 UI
  return (
    <div className="main-container">
      <div className="user-welcome">
        <h1 className="welcome-message">환영합니다, {userId}님! 👋</h1>
        <p className="welcome-subtitle">어떤 서비스를 이용하시겠습니까?</p>
      </div>

      <div className="service-section">
        <h2>계좌 관리</h2>
        <div className="service-grid">
          <div className="service-card">
            <div className="service-icon">💳</div>
            <h3>일반 계좌</h3>
            <p>계좌 개설 및 조회</p>
            <button className="btn-service">계좌 관리</button>
          </div>
          <div className="service-card">
            <div className="service-icon">💸</div>
            <h3>입출금</h3>
            <p>입금 및 출금</p>
            <button className="btn-service">거래하기</button>
          </div>
          <div className="service-card">
            <div className="service-icon">🔄</div>
            <h3>송금</h3>
            <p>다른 계좌로 송금</p>
            <button className="btn-service">송금하기</button>
          </div>
        </div>
      </div>

      <div className="service-section">
        <h2>적금 관리</h2>
        <div className="service-grid">
          <div className="service-card">
            <div className="service-icon">📊</div>
            <h3>적금 개설</h3>
            <p>1개월 / 6개월 / 1년</p>
            <button className="btn-service">적금 가입</button>
          </div>
          <div className="service-card">
            <div className="service-icon">💵</div>
            <h3>적금 입금</h3>
            <p>일일 납입</p>
            <button className="btn-service">납입하기</button>
          </div>
          <div className="service-card">
            <div className="service-icon">📋</div>
            <h3>적금 조회</h3>
            <p>적금 현황 확인</p>
            <button className="btn-service">조회하기</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AfterLoginPage;
