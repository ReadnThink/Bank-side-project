package com.side.workout.domain.transaction;

import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest // DB 관련된 Bean 다 올라온다.
class TransactionRepositoryImplTest extends DummyObject {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        autoincrementReset();
        dataSetting();
    }

    @Test
    void dataJpa_test1() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction -> {
            log.info("테스트 id: {}", transaction.getId());
            log.info("테스트 Sender: {}", transaction.getSender());
            log.info("테스트 Receiver: {}", transaction.getReceiver());
            log.info("테스트 Category: {}", transaction.getTransaction_category());
            log.info("테스트 : ===========================");
        }));
    }

    @Test
    void dataJpa_test2() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction -> {
            log.info("테스트 id: {}", transaction.getId());
            log.info("테스트 Sender: {}", transaction.getSender());
            log.info("테스트 Receiver: {}", transaction.getReceiver());
            log.info("테스트 Category: {}", transaction.getTransaction_category());
            log.info("테스트 : ===========================");
        }));
    }

    private void dataSetting() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(ssarAccount1, accountRepository));

        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(cosAccount, accountRepository));

        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository));

        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository));

        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository));
    }

    private void autoincrementReset(){
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE account ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE transaction ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

}