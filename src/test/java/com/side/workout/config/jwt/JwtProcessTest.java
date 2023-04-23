package com.side.workout.config.jwt;

import com.side.workout.config.auth.LoginUser;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class JwtProcessTest {

    private String createToken(){
        //given
        // 토큰 생성을 위해 토큰에 넣을 User 생성
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);

        //when
        String jwtToken = JwtProcess.create(loginUser);
        return jwtToken;
    }

    @Test
    void token_create_test() {
        //given

        //when
        String jwtToken = createToken();
        log.info("테스트 : {}", jwtToken);

        //then
        Assertions.assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }

    @Test
    void token_verify_test() {
        //given
        String token = createToken();
        String jwtToken = token.replace(JwtVO.TOKEN_PREFIX, ""); // Bearer 제거
        //when
        LoginUser loginUser2 = JwtProcess.verify(jwtToken);
        log.info("테스트 loginUser: {}", loginUser2.getUser().getId());
        //then
        assertThat(loginUser2.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser2.getUser().getRole()).isEqualTo(UserEnum.CUSTOMER);
    }
}