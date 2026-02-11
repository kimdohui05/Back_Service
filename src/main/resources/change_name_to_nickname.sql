-- User 테이블 수정: name, nickname, phone_number 컬럼 재구성
-- 실행 날짜: 2026-02-11

-- 최종 목표:
-- - name (VARCHAR(4)) - 이름 (최대 4글자)
-- - nickname (VARCHAR(10)) - 닉네임 (최대 10글자)
-- - phone_number (VARCHAR(11)) - 전화번호

-- 1단계: name 컬럼 길이를 4로 변경
ALTER TABLE User
MODIFY COLUMN name VARCHAR(4) NOT NULL;

-- 2단계: nickname 컬럼 길이를 10으로 변경
ALTER TABLE User
MODIFY COLUMN nickname VARCHAR(10) NOT NULL;

-- 3단계: phone_number 컬럼 확인 (이미 존재한다면 아무 작업 없음)
-- ALTER TABLE User
-- ADD COLUMN phone_number VARCHAR(11) NOT NULL;
