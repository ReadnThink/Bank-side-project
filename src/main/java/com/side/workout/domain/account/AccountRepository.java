package com.side.workout.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // select * from account where number = :number
    // checkpoint : 리펙토링 해야함 (계좌 소유자 확인시에 쿼리가 두번 나가기 때문에 join fetch 해야함)
    Optional<Account> findByAccountNumber(Long number);
}
