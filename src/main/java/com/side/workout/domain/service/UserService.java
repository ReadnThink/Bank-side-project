package com.side.workout.domain.service;

import com.side.workout.domain.handler.ex.CustomApiException;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.dto.user.UserRespDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.side.workout.dto.user.UserReqDto.*;
import static com.side.workout.dto.user.UserRespDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public JoinRespDto join(JoinReqDto joinReqDto){
        // 1. 동일 유저네임 존재 검사
        Optional<User> findUser = usersRepository.findByUsername(joinReqDto.getUsername());
        if(findUser.isPresent()){
            // 유저 네임이 중복되었다면
            throw new CustomApiException("동일한 username이 존재힙니다.");
        }
        // 2. 패스워드 인코딩
        // Persistence 에 들어갔다 나와서 객체뒤에 PS를 붙였습니다.
        User userPS = usersRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new JoinRespDto(userPS);
    }
}
