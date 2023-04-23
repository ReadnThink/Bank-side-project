-- Controller쪽에서는 전부 teardown을 사용할 것입니다.

-- 롤백을 사용하면 FK가 1부터 초기화되지 않습니다. -> TRUNCATE를 해야합니다.
-- drop(테이블 자체 삭제)을 하면 계속 create문이 나가기 때문에 TRUNCATE(테이블의 내용삭제)를 사용합니다.
-- DELETE는 로우를 하나씩 제거하는 반면, TRUNCATE는 테이블의 공간 자체를 통으로 날려버리기도 합니다.

-- 제약조건(FK)이 걸려있어 순서가 꼬이면 삭제가 되지 않습니다. 따라서 제약조건을 해제하고 삭제해야 합니다.
-- SET REFERENTIAL_INTEGRITY FALSE 모든 제약조건 해제 후 삭제
-- SET REFERENTIAL_INTEGRITY TRUE 제약조건 다시 작동
SET REFERENTIAL_INTEGRITY FALSE; -- FK 비활성화
truncate table transaction;
truncate table account;
truncate table users;
SET REFERENTIAL_INTEGRITY TRUE; -- FK 활성화