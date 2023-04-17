package com.side.workout.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.domain.user.UserEnum;
import com.side.workout.dto.ResponseDto;
import com.side.workout.util.CustomResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
public class SecurityConfig {
    @Bean // Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨. @Configuraion이 붙어있는 곳에만 @Bean을 쓸 수 있습니다.
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }

    //JWT 필터 등록 필요합니다.

    //JWT 서버를 만들어 Session을 사용하지 않습니다.
    //Bean으로 등록해 주어야 합니다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그 : filterChain 빈 등록");
        http.headers().frameOptions().disable(); // iframe 허용하지 않습니다.
        http.csrf().disable(); // enable이면 post맨 작동하지 않습니다.
        http.cors().configurationSource(configurationSource()); //다른 서버에 있는 프로그램으로부터의 자바스크립트 요청을 거부합니다. -> 허용해야 합니다.
        // jSessionId를 서버쪽에서 관리하지 않음 -> 세션을 사용하지 않습니다.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 로그인을 react, 앱으로 요청할 예정
        http.formLogin().disable();
        // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행합니다 -> 기능해제
        http.httpBasic().disable();

        // Exception 가로채기
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            String uri = request.getRequestURI();
            log.debug("디버그 : uri = {}", uri);
            if(uri.contains("admin")){
                CustomResponseUtil.unAuthorization(response, "관리자로 로그인을 진행해 주세요.");
            }else{
                CustomResponseUtil.unAuthentication(response, "로그인을 진행해 주세요");
            }
        });

        http.authorizeRequests()
                .antMatchers("/api/s/**").authenticated() // 인증 - 로그인이 되어야 합니다.
                .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN) //hasRole에는 String만 들어올 수 있습니다.
                .anyRequest().permitAll(); // 나머지 요청 전부 허용

        return http.build();
    }

    //자바스크립트 요청 허용
    public CorsConfigurationSource configurationSource() {
        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*"); // 모든 JS Header 허용
        configuration.addAllowedMethod("*"); // 모든 JS 메서드 허용
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청을 허용 -> 쿠키를 주고 받을 수 있습니다.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 주소요청에 위 설정을 적용
        return source;
    }
}
