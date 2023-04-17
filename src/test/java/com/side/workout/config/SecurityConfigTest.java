package com.side.workout.config;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Slf4j
@AutoConfigureMockMvc // Mock(가짜) 환경에 MockMvc 등록
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //Mockito Test로 가짜환경에서 테스트 하겠다는 뜻입니다.
class SecurityConfigTest {

    // 가짜 환경에 등록된 MockMvc 를 DI합니다.
    @Autowired
    private MockMvc mockMvc;

    @Test
    void authentication_test() throws Exception {
        // given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/test"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        log.info("디버그 : {}", responseBody);
        log.info("디버그 : {}", httpStatusCode);

        //then
        assertThat(httpStatusCode).isEqualTo(401);
    }


    @Test
    void authorization_test() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin/test"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        log.info("디버그 : {}", responseBody);
        log.info("디버그 : {}", httpStatusCode);

        //then
        assertThat(httpStatusCode).isEqualTo(403);

    }
}