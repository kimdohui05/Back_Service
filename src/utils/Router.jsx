/**
 * Router.jsx
 *
 * 역할: 애플리케이션의 라우팅 설정
 * 기능:
 * - URL 경로에 따라 페이지 전환
 * - 전역 스타일 적용 (GlobalStyle)
 * - 모든 페이지에 Header 컴포넌트 표시
 *
 * 라우트 구조:
 * / - 메인 페이지 (MainPage)
 * /login - 로그인 페이지 (LoginPage)
 * /signup - 회원가입 페이지 (SignupPage)
 */

import { createBrowserRouter, Outlet } from 'react-router-dom';
import { createGlobalStyle } from 'styled-components';
import MainPage from '../pages/MainPage';
import Header from '../components/Header';
import LoginPage from "../pages/LoginPage";
import SignupPage from "../pages/SignupPage";

const GlobalStyle = createGlobalStyle`
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  ::-webkit-scrollbar {
    width: 10px;
  }

  ::-webkit-scrollbar-track {
    background: #1f2029;
  }

  ::-webkit-scrollbar-thumb {
    background: #464858;
  }

  ::-webkit-scrollbar-thumb:hover {
    background: #777a91;
  }
`;

const router = createBrowserRouter([
  {
    path: '/',
    element: (
      <>
        <GlobalStyle />
        <Header />
        <Outlet />
      </>
    ),
    children: [
      {
        path: '',
        element: <MainPage />,
      },
      { path: "login", element: <LoginPage /> },
      { path: "signup", element: <SignupPage /> },
    ],
  },
]);

export default router;