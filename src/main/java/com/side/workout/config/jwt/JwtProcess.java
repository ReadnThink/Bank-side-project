package com.side.workout.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.side.workout.config.auth.LoginUser;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtProcess {

    //토큰 생성
    public static String create(LoginUser loginUser){
        String jwtToken = JWT.create()
                .withSubject("work out") //아무거나 적어도 됩니다.
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIM))
                .withClaim("id", loginUser.getUser().getId())
                .withClaim("role", loginUser.getUser().getRole().name())
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
        return JwtVO.TOKEN_PREFIX+jwtToken;
    }

    //토큰 검증 (return 되는 LoginUser 객체를 강제로 시큐리티 세션에 집접 주입합니다.)
    public static LoginUser verify(String token){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        // 유저 객체 생성
        User user = User.builder().id(id).role(UserEnum.valueOf(role)).build();
        // UserDetail 타입만 컨텍스트 홀더에 들어갈 수 있기에 넣어줍니다.
        LoginUser loginUser = new LoginUser(user);
        return loginUser;
    }
}
