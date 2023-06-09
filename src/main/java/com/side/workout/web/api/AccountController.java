package com.side.workout.web.api;

import com.side.workout.config.auth.LoginUser;
import com.side.workout.dto.ResponseDto;
import com.side.workout.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.side.workout.dto.account.AccountReqDto.*;
import static com.side.workout.dto.account.AccountRespDto.*;

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

    @DeleteMapping("/s/account/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountNumber, @AuthenticationPrincipal LoginUser loginUser){
        accountService.deleteAccount(accountNumber, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌삭제 성공", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit") // 인증 필요없음
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto, BindingResult bindingResult){
        AccountDepositRespDto accountDepositRespDto = accountService.deposit(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌입금 성공", accountDepositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto, BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        AccountWithdrawRespDto accountWithdrawRespDto = accountService.withdraw(accountWithdrawReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌출금 성공", accountWithdrawRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountTransferReqDto accountTransferReqDto, BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        AccountTransferRespDto accountTransferRespDto = accountService.transfer(accountTransferReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌이체 성공", accountTransferRespDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/{number}")
    public ResponseEntity<?> findDetailAccount(@PathVariable Long number,
                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                             @AuthenticationPrincipal LoginUser loginUser){
        AccountDetailRespDto accountDetailRespDto = accountService.getAccountDetail(number, loginUser.getUser().getId(), page);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌상세보기 성공", accountDetailRespDto), HttpStatus.OK);
    }

}
