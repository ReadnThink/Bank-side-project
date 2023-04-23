package com.side.workout.service;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.dto.account.AccountRespDto.AccountListRespDto;
import com.side.workout.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.side.workout.dto.account.AccountReqDto.AccountCreateReqDto;
import static com.side.workout.dto.account.AccountRespDto.AccountCreateRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AccountListRespDto findAccountList(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다.")
        );

        // 유저의 모든 계좌 목록
        List<Account> accountListPS = accountRepository.findByUsers_id(userPS.getId());
        return new AccountListRespDto(userPS, accountListPS);
    }

    @Transactional // 커밋해야함
    public AccountCreateRespDto createAccount(AccountCreateReqDto accountCreateReqDto, Long userId){
        // User DB에 있는지 검증 및 유저 엔티티 가져오기
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다.")
        );

        // 해당 계좌가 DB에 있는 중복여부 체크
        Optional<Account> account = accountRepository.findByAccountNumber(accountCreateReqDto.getNumber());
        if(account.isPresent()){
            throw new CustomApiException("해당 계좌가 이미 존재합니다.");
        }

        // 계좌 등록
        Account accountPS = accountRepository.save(accountCreateReqDto.toEntity(userPS));

        // DTO 응당
        return new AccountCreateRespDto(accountPS);
    }

    @Transactional
    public void deleteAccount(Long accountNumber, Long userId){
        // 1. 계좌 확인
        Account accountPS = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")
        );
        // 2. 계좌 소유자 확인
        accountPS.checkOwner(userId); // 예외 발생시 핸들러에서 처리

        // 3. 계좌 삭제
        accountRepository.deleteById(accountPS.getId());
    }
}
