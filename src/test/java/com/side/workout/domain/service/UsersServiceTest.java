package com.side.workout.domain.service;

import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.dto.user.UserRespDto;
import com.side.workout.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.side.workout.dto.user.UserReqDto.JoinReqDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
// Spring 관련 Bean들이 하나도 없는 환경입니다.
@ExtendWith(MockitoExtension.class) // Junit것을 사용
class UsersServiceTest extends DummyObject {
    // 진짜 = Spy, 가짜 = Mock
    @InjectMocks //가짜 Service를 주입합니다.
    private UserService userService;
    @Mock // 가짜로 만들어 주는 것입니다.
    private UserRepository userRepository;
    @Spy //Spy는 Spring IOC에 있는 것을 InjectMocks에 넣습니다.
    private BCryptPasswordEncoder passwordEncoder;
    @Test
    void join() {

        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("userA");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("userA@nate.com");
        joinReqDto.setFullname("test UserA");

        //stub -> 가정한다는 것입니다.
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        User userA = newMockUser(1L, "userA","test UserA");
        when(userRepository.save(any())).thenReturn(userA);

        //when
        UserRespDto.JoinRespDto joinRespDto = userService.join(joinReqDto);
        log.info("test = {}", joinRespDto);
        //then
        assertThat(joinRespDto.getId()).isEqualTo(1L);
        assertThat(joinRespDto.getUsername()).isEqualTo(userA.getUsername());
    }
}