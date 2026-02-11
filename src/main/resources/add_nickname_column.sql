-- User 테이블에 nickname 컬럼 추가
-- 이 SQL 파일을 직접 MySQL/MariaDB에서 실행해주세요

-- 1. nickname 컬럼 추가
ALTER TABLE User ADD COLUMN nickname VARCHAR(20) NOT NULL;

-- 확인: User 테이블 구조 보기
-- DESC User;
