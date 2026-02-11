-- 모든 테이블 데이터 삭제 (테스트 초기화용)
-- 실행 날짜: 2026-02-11

-- 외래키 체크 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 1. account 테이블 데이터 전체 삭제
TRUNCATE TABLE account;

-- 2. savings 테이블 데이터 전체 삭제
TRUNCATE TABLE savings;

-- 3. user 테이블 데이터 전체 삭제
TRUNCATE TABLE user;

-- 외래키 체크 활성화
SET FOREIGN_KEY_CHECKS = 1;

-- 완료
SELECT '모든 데이터가 삭제되었습니다. 처음부터 테스트할 수 있습니다.' AS message;
