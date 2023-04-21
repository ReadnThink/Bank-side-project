package com.side.workout.config.jwt;

import com.side.workout.config.auth.LoginUser;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test") // test yml을 사용
@AutoConfigureMockMvc // Mockito 환경에 mockMvc를 DI하려면 필요합니다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //가짜 환경 시작
class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authorization_success_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        log.info("테스트 : {}", jwtToken);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello/test").header(JwtVO.HEADER,jwtToken));
        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void authorization_fail_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello/test"));
        //then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void authorization_admin_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        log.info("테스트 : {}", jwtToken);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin/hello/test").header(JwtVO.HEADER,jwtToken));
        //then
        resultActions.andExpect(status().isForbidden());
    }
}