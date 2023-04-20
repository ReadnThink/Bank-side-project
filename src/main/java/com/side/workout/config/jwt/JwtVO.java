package com.side.workout.config.jwt;

/**
 * SECRET KEY는 노출되면 안됩니다. -> 환경변수 주입
 * 리프레시 토큰 (추후에 하겠습니다.)
 */
public interface JwtVO {
    public static final String SECRET = "test"; // 대칭키
    public static final int EXPIRATION_TIM = 1000 * 60 * 60 * 24 * 7; // 일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
}
