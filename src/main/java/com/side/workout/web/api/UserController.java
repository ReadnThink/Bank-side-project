package com.side.workout.web.api;

import com.side.workout.service.UserService;
import com.side.workout.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.side.workout.dto.user.UserReqDto.JoinReqDto;
import static com.side.workout.dto.user.UserRespDto.JoinRespDto;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController // Json 통신을 위함
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinReqDto joinReqDto, BindingResult bindingResult){ // @RequestBody를 통해 자바 객체로 받아야 합니다.
        JoinRespDto joinRespDto = userService.join(joinReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", joinRespDto), HttpStatus.CREATED); // 201 코드 : 새로운 리소스 생성
    }
}
