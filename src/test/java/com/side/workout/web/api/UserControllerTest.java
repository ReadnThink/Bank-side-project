package com.side.workout.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.side.workout.dto.user.UserReqDto.JoinReqDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 통합테스트 환경 -> 컨트롤러는 통합 테스트를 해야합니다.
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private ObjectMapper om;

    @BeforeEach
    public void setUp(){
        dataSetting();
    }
    @AfterEach
    public void setDown(){
        userRepository.deleteAll();
    }

    @Test
    void join_success_test() throws Exception {
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("userA");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("userA@nate.com");
        joinReqDto.setFullname("userA");

        String requestBody = om.writeValueAsString(joinReqDto);
//        log.info("Om을 통해 Json으로 변환된 자바 객체 = {}", requestBody);

        //when
        // content에는 Json이 들어가야 합니다. 현재 joinReqDto는 자바객체이기 때문에 ObjectMapper를 통해 Json으로 변환해주어야 합니다.
        ResultActions resultActions = mockMvc.perform(post("/api/join").content(requestBody)
                .contentType(MediaType.APPLICATION_JSON) // Body값을 설명해주는 미디어타입이 꼭 필요합니다.
        );

//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        log.info("mockMvc에서 반환된 객체 = {}", responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void join_fail_test() throws Exception {
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("userB");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("userA@nate.com");
        joinReqDto.setFullname("userB");

        String requestBody = om.writeValueAsString(joinReqDto);
//        log.info("Om을 통해 Json으로 변환된 자바 객체 = {}", requestBody);

        //when
        // content에는 Json이 들어가야 합니다. 현재 joinReqDto는 자바객체이기 때문에 ObjectMapper를 통해 Json으로 변환해주어야 합니다.
        ResultActions resultActions = mockMvc.perform(post("/api/join").content(requestBody)
                .contentType(MediaType.APPLICATION_JSON) // Body값을 설명해주는 미디어타입이 꼭 필요합니다.
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    private void dataSetting(){
        // 중복 아이디를 테스트하기 위해 만듬
        userRepository.save(newUser("userB","userB"));
    }
}