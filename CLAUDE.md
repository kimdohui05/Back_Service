# BankService Backend - Claude AI Context

## 프로젝트 개요

**프로젝트명**: BankService (은행 서비스 백엔드)
**기술 스택**: Spring Boot 3.5.7, MySQL, Spring Data JDBC, Gradle

---

## 📚 문서 읽기

새로운 세션 시작 시 또는 "docs 읽어" 명령 시, 다음 경로의 문서를 읽어주세요:

```
C:\Users\kimdo\OneDrive\바탕 화면\개인 프로젝트\docs
```

### 필수 문서 목록

1. **README-FIRST.md** - 프로젝트 개요 및 최신 변경사항
2. **database-schema.md** - 데이터베이스 스키마
3. **PROJECT-STRUCTURE.md** - 프로젝트 구조
4. **AUTOMATIC-INTEREST-SYSTEM.md** - 일반 계좌 이자 시스템
5. **SAVINGS-SYSTEM-V3.md** - 적금 시스템 (최신 버전)
6. **REQUIREMENTS-HISTORY.md** - 요구사항 변경 이력
7. **FRONTEND-GUIDE.md** - 프론트엔드 가이드

---

## 🎯 빠른 참조

### 주요 디렉토리
- `src/main/java/com/example/bankservice/user/` - 사용자 관리
- `src/main/java/com/example/bankservice/account/` - 일반 계좌
- `src/main/java/com/example/bankservice/savings/` - 적금 계좌

### 데이터베이스
- **이름**: bank_service
- **테이블**: user, account, savings

### API 엔드포인트
- User: `/api/user/*`
- Account: `/api/account/*`
- Savings: `/api/savings/*`

---

## 🔄 최근 변경사항 (2026-02-11)

### User 테이블 구조 변경
- `name`: 최대 4글자 (한글/영문/숫자만)
- `nickname`: 최대 10글자 (한글/영문/숫자만, 새로 추가)
- `phone_number`: 11자리

### 회원가입 검증 규칙
- ✅ 비밀번호 ≠ 아이디
- ✅ 이름/닉네임 특수문자 불가
- ✅ 길이 제한 검증

---

## 📝 작업 규칙

1. 코드 변경 시 관련 문서도 함께 업데이트
2. 새로운 기능 추가 시 `REQUIREMENTS-HISTORY.md`에 기록
3. 데이터베이스 스키마 변경 시 `database-schema.md` 업데이트
4. SQL 마이그레이션 파일은 `src/main/resources/`에 저장

---

**마지막 업데이트**: 2026-02-11
