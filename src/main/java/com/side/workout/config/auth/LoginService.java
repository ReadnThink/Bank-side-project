package com.side.workout.config.auth;

import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 시큐리티 세션을 만들어 줍니다.
    // 시큐리티 로그인 시 시큐리티가 loadUserByUsername() 실행해서 username을 체크합니다.
    // 없다면 오류
    // 있다면 정상적으로 시큐리티 컨텍스트 내부 세션에 로그인된 세션이 만들어집니다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPs = userRepository.findByUsername(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패")
        );
        return new LoginUser(userPs);
    }
}
