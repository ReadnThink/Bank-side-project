package com.side.workout.config.dummy;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev") // prod 모드에서는 실행되면 안된다.
    @Bean
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository) {
            return (args -> {
                // 서버 실행시에 무조건 실행됩니다.
                User userA = userRepository.save(newUser("userA", "유저A"));
                User userB = userRepository.save(newUser("userB", "유저B"));

                Account userAAccount = accountRepository.save(newAccount(1111L, userA));
                Account userBAccount = accountRepository.save(newAccount(2222L, userB));
            });
    }
}
