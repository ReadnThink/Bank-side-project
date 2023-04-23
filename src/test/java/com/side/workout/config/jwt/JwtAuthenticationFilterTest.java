package com.side.workout.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.side.workout.dto.user.UserReqDto.LoginReqDto;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
//@Transactional //테스트 환경에서 종료시 롤백
@Sql("classpath:db/teardown.sql") // SpringBootTest 통합테스트 하는곳에 전부 teardown.sql을 붙여주자 -> beforeEach 실행 직전 마다 실행됩니다.
@ActiveProfiles("test") // test yml을 사용
@AutoConfigureMockMvc // Mockito 환경에 mockMvc를 DI하려면 필요합니다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //가짜 환경 시작
class JwtAuthenticationFilterTest extends DummyObject {
    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(newUser("userA","1234"));
    }

    @Test
    void successfulAuthentication() throws Exception {
        //given
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("userA");
        loginReqDto.setPassword("1234");
        String requestBody = om.writeValueAsString(loginReqDto);
        log.info("테스트 : {}",requestBody);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        log.info("테스트 : {}", responseBody);
        log.info("테스트 : {}", jwtToken);

        //then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        Assertions.assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("userA"));
    } //종료시 롤백

    @Test
    void unsuccessfulAuthentication() throws Exception {
        //given
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("");
        loginReqDto.setPassword("12345");
        String requestBody = om.writeValueAsString(loginReqDto);
        log.info("테스트 : {}",requestBody);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        log.info("테스트 : {}", responseBody);
        log.info("테스트 : {}", jwtToken);

        //then
        resultActions.andExpect(status().isUnauthorized());
    } //종료시 롤백
}