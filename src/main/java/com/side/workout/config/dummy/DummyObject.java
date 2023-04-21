package com.side.workout.config.dummy;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyObject {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Entity save 용도
    protected User newUser(String username, String fullname){
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username+"@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    // 가짜 stub -> when.thenReturn 구조에서 사용
    protected User newMockUser(Long id, String username, String fullname){
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username+"@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }
    protected Account newAccount(Long number, User user){
        return Account.builder()
                .accountNumber(number)
                .password(1234L)
                .balance(1000L)
                .users(user)
                .build();
    }
    protected Account newMockAccount(Long id, Long number, Long balance, User user){
        return Account.builder()
                .id(id)
                .accountNumber(number)
                .password(1234L)
                .balance(balance)
                .users(user)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }
}
