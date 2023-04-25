package com.side.workout.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.handler.ex.CustomApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import static com.side.workout.dto.account.AccountReqDto.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
//@Transactional //테스트 환경에서 종료시 롤백
@Sql("classpath:db/teardown.sql") // SpringBootTest 통합테스트 하는곳에 전부 teardown.sql을 붙여주자 -> beforeEach 실행 직전 마다 실행됩니다.
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 통합테스트 환경 -> 컨트롤러는 통합 테스트를 해야합니다.
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        User userA = userRepository.save(newUser("userA", "유저A"));
        User userB = userRepository.save(newUser("userB", "유저B"));

        Account userAAccount = accountRepository.save(newAccount(1111L, userA));
        Account userBAccount = accountRepository.save(newAccount(2222L, userB));

        em.clear();
    }

    // jwt token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메서드 실행전에 수행됩니다!) -> 옵션필요 (setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // setupBefore = TestExecutionEvent.TEST_EXECUTION (create_account() 메서드 전에 수행)
    @WithUserDetails(value = "userA", setupBefore = TestExecutionEvent.TEST_EXECUTION) // DB에서 username = userA 조회 후 세션에 담아주는 어노테이션입니다.
    @Test
    void create_account() throws Exception {
        //given
        AccountCreateReqDto accountCreateReqDto = new AccountCreateReqDto();
        accountCreateReqDto.setNumber(9999L);
        accountCreateReqDto.setPassword(1234L);
        String requestBody = om.writeValueAsString(accountCreateReqDto);
        log.info("테스트 : requestBody {}", requestBody);

        //when
        ResultActions resultActions = mockMvc.
                perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("테스트 : responseBody {}", responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    /**
     *  delete는 Http 바디가 없기떄문에 content, contentType이 필요 없습니다.
     *
     *  Lazy 로딩이여도 id를 조회할 때는 select 쿼리가 날라가지 않는다.
     *    - 영속성 컨텍스트에 들어가는 조건은 Id값이 무조건 있어야 하기때문에 id만 조회하는경우에는 쿼리를 날리지 않습니다.
     *    - 만약 id가 아닌 다른 필드를 조회한다면, 쿼리를 날립니다. (영속성 컨텍스트에 없을 경우에만)
     *
     *  Test시에 insert 한것들이 전부 영속성 컨텍스트에 올라갑니다.
     *  영속화 된것들을 초기화 해주는것이 개발모드와 동일한 환경으로 테스트를 할 수 있게 해줍니다.
     *
     *  최초 select는 쿼리가 발생하지만, 영속성 컨텍스트에 있다면 1차캐시에 있는 객체를 조회하여 쿼리가 날라가지 않습니다.
     *  즉, Lazy 로딩은 PC에 있다면 쿼리 발생 x
     *  Lazy로딩 시 PC에 없다면 쿼리 발생
     */
    @WithUserDetails(value = "userA", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void delete_account_success() throws Exception {
        //given
        Long accountNumber = 1111L;
        //when
        ResultActions resultActions = mockMvc.
                perform(delete("/api/s/account/" + accountNumber));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("테스트 : responseBody {}", responseBody);

        //then
        //Junit 테스트에서 delete 쿼리는 DB관련(DML)마지막 실행되면 발동안됨.
        Assertions.assertThrows(CustomApiException.class, ()-> accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")
        ));
    }

    //실패 - 계좌 소유자가 아님

    //실패 - 없는 계좌


    /**
     *  Dto 잘 만들어졌는지 ObjectMapper 확인은 공식!
     */
    @Test
    void deposit_account_test() throws Exception {
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setCategory("DEPOSIT");
        accountDepositReqDto.setTel("01011112222");

        String requestBody = om.writeValueAsString(accountDepositReqDto);
        log.info("테스트 : requestBody {}", requestBody);

        //when
        ResultActions resultActions = mockMvc.
                perform(post("/api/account/deposit").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("테스트 : responseBody {}", responseBody);

        //then
        resultActions.andExpect(status().isCreated());
        /**
         * 잔액이 확인하고 싶다면 TransactionDto 에서 @JsonIgnore 지우고 depositAccountBalance를 확인할 수 있다.
         */
    }

    @WithUserDetails(value = "userA", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void withdrawAccount_test() throws Exception {
        //given
        AccountWithdrawReqDto accountWithdrawReqDto = new AccountWithdrawReqDto();
        accountWithdrawReqDto.setNumber(1111L);
        accountWithdrawReqDto.setPassword(1234L);
        accountWithdrawReqDto.setAmount(100L);
        accountWithdrawReqDto.setCategory("WITHDRAW");

        String requestBody = om.writeValueAsString(accountWithdrawReqDto);
        log.info("테스트 : requestBody {}", requestBody);
        //when
        ResultActions resultActions = mockMvc.
                perform(post("/api/s/account/withdraw").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("테스트 : responseBody {}", responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @WithUserDetails(value = "userA", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void TransferAccount_test() throws Exception {
        //given
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setCategory("TRANSFER");

        String requestBody = om.writeValueAsString(accountTransferReqDto);
        log.info("테스트 : requestBody {}", requestBody);
        //when
        ResultActions resultActions = mockMvc.
                perform(post("/api/s/account/transfer").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("테스트 : responseBody {}", responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }
}
