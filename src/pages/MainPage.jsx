/**
 * MainPage.jsx
 *
 * 역할: 메인 페이지 (홈 화면)
 * 기능:
 * - 로그인 상태 확인
 * - 로그인 전/후 컴포넌트 조건부 렌더링
 */

import { useState, useEffect } from "react";
import BeforeLoginPage from "../components/BeforeLoginPage";
import AfterLoginPage from "../components/AfterLoginPage";
import "../css/MainPage.css";

function MainPage() {
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

    checkLoginStatus();
    window.addEventListener("loginStatusChanged", checkLoginStatus);

    return () => {
      window.removeEventListener("loginStatusChanged", checkLoginStatus);
    };
  }, []);

  return (
    <div className="main-page">
      {isLoggedIn ? (
        <AfterLoginPage userId={userId} />
      ) : (
        <BeforeLoginPage />
      )}
    </div>
  );
}

export default MainPage;
