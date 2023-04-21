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
    @Test
    void token_create_test() {
        //given
        // 토큰 생성을 위해 토큰에 넣을 User 생성
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);

        //when
        String jwtToken = JwtProcess.create(loginUser);
        log.info("테스트 : {}", jwtToken);

        //then
        Assertions.assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }

    @Test
    void token_verify_test() {
        //given
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3b3JrIG91dCIsInJvbGUiOiJDVVNUT01FUiIsImlkIjoxLCJleHAiOjE2ODI2NjcxNjJ9.FS9CRoPsxEsIqPFxhyZEduxJo1MYuRfvcghClMb85ra7qQuXYDRAj6aAeMtKtv1u0etG4UFBac4RG4L7DLe9yw";
        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        log.info("테스트 loginUser: {}", loginUser.getUser().getId());
        //then
        assertThat(loginUser.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser.getUser().getRole()).isEqualTo(UserEnum.CUSTOMER);
    }
}