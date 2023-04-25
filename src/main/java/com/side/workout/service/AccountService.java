package com.side.workout.service;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.domain.transaction.TransactionCategory;
import com.side.workout.domain.transaction.TransactionRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.side.workout.dto.account.AccountReqDto.*;
import static com.side.workout.dto.account.AccountRespDto.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

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

    @Transactional
    public AccountDepositRespDto deposit(AccountDepositReqDto accountDepositReqDto){
        // 0원 체크
        if(accountDepositReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }
        // 입금 계좌 확인
        Account depositAccountPS = accountRepository.findByAccountNumber(accountDepositReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다.")
                );
        //입금 (해당 계좌 balance 조정 - update(더티체킹))
        depositAccountPS.deposit(accountDepositReqDto.getAmount());

        //거래 내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(depositAccountPS)
                .withdrawAccountBalance(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountDepositReqDto.getAmount())
                .transaction_category(TransactionCategory.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositReqDto.getNumber() + "")
                .tel(accountDepositReqDto.getTel())
                .build();
        Transaction transactionPS = transactionRepository.save(transaction);

        // dto 응답 (객체로 소통하면 하나하나 대입하는것보다 개발 및 유지보수에 효과적이다)
        return new AccountDepositRespDto(depositAccountPS, transactionPS);
    }

    @Transactional
    public AccountWithdrawRespDto withdraw(AccountWithdrawReqDto accountWithdrawReqDto, Long userId){
        // 0원 체크
        if(accountWithdrawReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        // 출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByAccountNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다.")
                );
        // 출금 소유자 확인 (로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);
        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkPassword(accountWithdrawReqDto.getPassword());
        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());
        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());
        //거래 내역 남기기 (내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .transaction_category(TransactionCategory.WITHDRAW)
                .sender(accountWithdrawReqDto.getNumber()+"")
                .receiver("ATM")
                .build();
        Transaction transactionPS = transactionRepository.save(transaction);
        // DTO 응답
        return new AccountWithdrawRespDto(withdrawAccountPS, transactionPS);
    }



}
