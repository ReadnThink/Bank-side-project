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
    void findTransactionList_ALL_test() {
        // given
        Long accountId = 1L;

        //when

        //then
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
        User user = userRepository.save(newUser("user", "유저"));
        User test = userRepository.save(newUser("test", "테스트"));
        User good = userRepository.save(newUser("good", "굿"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account userAccount1 = accountRepository.save(newAccount(1111L, user));
        Account testAccount = accountRepository.save(newAccount(2222L, test));
        Account goodAccount = accountRepository.save(newAccount(3333L, good));
        Account userAccount2 = accountRepository.save(newAccount(4444L, user));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(userAccount1, accountRepository));

        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(testAccount, accountRepository));

        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(userAccount1, testAccount, accountRepository));

        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(userAccount1, goodAccount, accountRepository));

        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(testAccount, userAccount1, accountRepository));
    }

    private void autoincrementReset(){
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE account ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE transaction ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

}