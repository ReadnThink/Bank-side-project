package com.side.workout.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.handler.ex.CustomApiException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.side.workout.dto.account.AccountReqDto.AccountCreateReqDto;
import static com.side.workout.dto.account.AccountRespDto.AccountCreateRespDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks // 모든 Mock 들이 InjectMocks 로 주입됨
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock // 실제 기능을 못하기 때문에 when, given을 정의해주어야 합니다.
    private AccountRepository accountRepository;

    @Spy // 진짜로 사용해야하기때문에 Spy를 사용합니다.
    private ObjectMapper om;

    @Test
    void create_account() throws JsonProcessingException {
        //given
        Long userId = 1L;

        AccountCreateReqDto accountCreateReqDto = new AccountCreateReqDto();
        accountCreateReqDto.setNumber(1111L);
        accountCreateReqDto.setPassword(1234L);

        // stub1
        User userA = newMockUser(userId, "userA", "userA");
        when(userRepository.findById(any())).thenReturn(Optional.of(userA));

        // stub2
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.empty());

        // stub3
        Account userAAccount = newMockAccount(1L, 1111L, 1000L, userA);
        when(accountRepository.save(any())).thenReturn(userAAccount);

        //when
        AccountCreateRespDto accountCreateRespDto = accountService.createAccount(accountCreateReqDto, userId);
        String responseBody = om.writeValueAsString(accountCreateRespDto);
        log.info("테스트 : {}", responseBody);
        //then
        Assertions.assertThat(accountCreateRespDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    void delete_account_success() {
        //given
        Long accountNumber = 1111L;
        Long userId = 1L;

        // stub
        User userA = newMockUser(userId, "userA", "유저A");
        Account userAAccount = newMockAccount(userId, 1111L, 1000L, userA);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(userAAccount));

        //when
        accountService.deleteAccount(accountNumber, userId);

        //then
    }

    @Test
    void delete_account_fail_유저아이디불일치() {
        //given
        Long accountNumber = 1111L;
        Long userId = 1L;
        Long failUserId = 2L;

        // stub
        User userA = newMockUser(userId, "userA", "유저A");
        Account userAAccount = newMockAccount(userId, 1111L, 1000L, userA);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(userAAccount));

        //when
        assertThrows(CustomApiException.class, () -> accountService.deleteAccount(accountNumber, failUserId));
    }
}