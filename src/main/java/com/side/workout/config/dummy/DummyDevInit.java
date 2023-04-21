package com.side.workout.config.dummy;

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
    CommandLineRunner init(UserRepository userRepository) {
            return (args -> {
                // 서버 실행시에 무조건 실행됩니다.
                User user = userRepository.save(newUser("userA", "userA"));
            });
    }
}
