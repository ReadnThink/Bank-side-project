package com.side.workout.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.domain.transaction.TransactionRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TransactionControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        dataSetting();
        em.clear();
    }

    @WithUserDetails(value = "userA", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void findTransactionList_test() throws Exception {
        // given
        Long number = 1111L;
        String category = "ALL";
        String page = "0";

        //when
        ResultActions resultActions = mockMvc.
                perform(get("/api/s/account/"+number+"/transaction").param("category", category).param("page", page));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("테스트 : responseBody {}", responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
        resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
        resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
        resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));
    }

    private void dataSetting() {
        User user = userRepository.save(newUser("userA", "유저"));
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
}