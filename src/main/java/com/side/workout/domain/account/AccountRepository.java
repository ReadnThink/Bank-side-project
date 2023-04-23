package com.side.workout.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // select * from account where number = :number
    // checkpoint : 리펙토링 해야함 (계좌 소유자 확인시에 쿼리가 두번 나가기 때문에 join fetch 해야함)
    //              -> account.getUser().getId()로 id를 조회하기 때문에 리펙토링이 필요 없습니다.
    //              -> 만약 id 이외의 필드값을 조회한다면 쿼리가 2번 나갈 수 있습니다. -> 이때 JOIN FETCH 쿼리문이 필요합니다.

//    @Query("SELECT ac FROM Account ac JOIN FETCH ac.users u WHERE ac.accountNumber = :number")
    Optional<Account> findByAccountNumber(Long number);

    // select * from account where users_id = :id -> 내 계좌 전체조회
    List<Account> findByUsers_id(Long id);

}
