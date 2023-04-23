package com.side.workout.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.auth.LoginUser;
import com.side.workout.util.CustomResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.side.workout.dto.user.UserReqDto.LoginReqDto;
import static com.side.workout.dto.user.UserRespDto.LoginRespDto;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
    }

    /**
     *     Post : /api/login 일때 동작
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("디버그 : attemptAuthentication 호출됨 -> 로그인 시도");
        try{
            ObjectMapper om = new ObjectMapper();
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

            // 토큰을 만들어 강제 로그인
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(), loginReqDto.getPassword()
            );
            // UserDetailsService의 loadUserByUsername 호출
            // JWT를 쓴다고 해도, 컨트롤러 진입을 하면 시큐리티의 권한체크, 인증체크의 기능을 사용할 수 있도록 세션을 만드는것
            // 세션의 유효기간은 request하고, response하면 끝나게 됩니다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            return authentication;
        }catch (Exception e){
            //예외 발생하면 authenticationEntryPoint에서 예외를 처리 -> Controller로 가는게 아니라 가기 전에 처리되는것
            // 우선 unsuccessfulAuthentication 호출
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    /**
     * 로그인 실패시 실행
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CustomResponseUtil.fail(response,"로그인실패", HttpStatus.UNAUTHORIZED);
    }

    /**
     * attemptAuthentication의 return authentication 잘 작동이 되면 실행
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("디버그 : successfulAuthentication 호출됨 로그인 세션 등록 완료");
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        // 헤더에 토큰 담아서 응답
        response.addHeader(JwtVO.HEADER, jwtToken);

        LoginRespDto loginRespDto = new LoginRespDto(loginUser.getUser());
        // 토큰을 만들어 응답
        CustomResponseUtil.success(response,loginRespDto);
    }
}
