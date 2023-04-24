package com.side.workout.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.domain.transaction.TransactionCategory;
import com.side.workout.domain.transaction.TransactionRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.dto.account.AccountRespDto.AccountListRespDto;
import com.side.workout.handler.ex.CustomApiException;
import com.side.workout.util.CustomDateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
                .depositAccount(depositAccountPS)
                .withdrawAccount(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .withdrawAccountBalance(null)
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

    @Setter
    @Getter
    public static class AccountDepositRespDto{
        private Long id; // 계좌 id
        private Long number; // 계좌번호
        private TransactionDto transaction;

        public AccountDepositRespDto(Account account, Transaction trancaction) {
            this.id = account.getId();
            this.number = account.getAccountNumber();
            this.transaction = new TransactionDto(trancaction); // dto로 만드는 이유는 Entity를 컨트롤러에 노출하게되면 순환참조 발생 위험이 있다. (어노테이션 사용시 순환참조 발생)
        }

        @Setter
        @Getter
        public class TransactionDto{
            private Long id;
            private String category;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createAt;
            @JsonIgnore // 클라이언트에게 전달x -> 서비스단에서 테스트 용도
            private Long depositAccountBalance;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.category = transaction.getTransaction_category().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreateAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDepositReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 10)
        private Long number;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String category; // DEPOSIT
        @Pattern(regexp = "^[0-9]{11}")
        private String tel;
    }


}
