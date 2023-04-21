package com.side.workout.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.side.workout.dto.account.AccountReqDto.AccountCreateReqDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 통합테스트 환경 -> 컨트롤러는 통합 테스트를 해야합니다.
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        User userA = userRepository.save(newUser("userA", "유저A"));
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
        ResultActions resultActions = mockMvc.perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.info("테스트 : responseBody {}", responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }
}