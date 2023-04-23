package com.side.workout.web.api;

import com.side.workout.config.auth.LoginUser;
import com.side.workout.dto.ResponseDto;
import com.side.workout.dto.account.AccountListRespDto;
import com.side.workout.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.side.workout.dto.account.AccountReqDto.AccountCreateReqDto;
import static com.side.workout.dto.account.AccountRespDto.AccountCreateRespDto;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountCreateReqDto accountCreateReqDto,
                                           BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser){
        AccountCreateRespDto accountCreateRespDto = accountService.createAccount(accountCreateReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountCreateRespDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findAccountList(@AuthenticationPrincipal LoginUser loginUser){
        AccountListRespDto accountList = accountService.findAccountList(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌목록조회_유저별 조회 성공", accountList), HttpStatus.OK);
    }
}
