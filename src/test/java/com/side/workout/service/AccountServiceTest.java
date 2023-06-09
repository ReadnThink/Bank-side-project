package com.side.workout.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.config.dummy.DummyObject;
import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.domain.transaction.TransactionCategory;
import com.side.workout.domain.transaction.TransactionRepository;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserRepository;
import com.side.workout.handler.ex.CustomApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.side.workout.dto.account.AccountReqDto.*;
import static com.side.workout.dto.account.AccountRespDto.AccountCreateRespDto;
import static com.side.workout.dto.account.AccountRespDto.AccountDepositRespDto;
import static org.assertj.core.api.Assertions.assertThat;
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
    @Mock
    private TransactionRepository transactionRepository;

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
        assertThat(accountCreateRespDto.getNumber()).isEqualTo(1111L);
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

    // Account -> balance 변경되었는지
    // Transaction -> balance 잘 기록하는지
    @Test
    void deposit_test1() {
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setCategory("DEPOSIT");
        accountDepositReqDto.setTel("01011112222");

        /**
         * when() -> 괄호 안에 있는 객체를 갖고있는 객체(Service)가 실행될때 실행된다.
         * 테스트가 시작할 때 동작하는게 아니다.
         */

        // stub 1
        User userA = newMockUser(1L, "userA", "유저A");
        Account userAAccount1 = newMockAccount(1L, 1111L, 1000L, userA);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(userAAccount1)); // service 호출 후 실행

        // stub 2 (stub이 진행될 때마다 연관된 객체는 새로 만들어서 주입해야 한다. -> 타이밍 때문에 꼬인다.)
        Account userAAccount2 = newMockAccount(1L, 1111L, 1000L, userA);
        Transaction transaction = newMockDepositTransaction(1L, userAAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction); // service 호출 후 실행

        //when
        AccountDepositRespDto accountDepositRespDto = accountService.deposit(accountDepositReqDto);
        log.info("테스트 : 트랜잭션 입금계좌 잔액 = {}", accountDepositRespDto.getTransaction().getDepositAccountBalance());
        log.info("테스트 : 계좌 잔액 = {}", userAAccount1.getBalance());
        log.info("테스트 : 계좌 잔액 = {}", userAAccount2.getBalance());

        //then
        assertThat(userAAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositRespDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
    }

    @Test
    void deposit_test2() throws JsonProcessingException {
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setCategory("DEPOSIT");
        accountDepositReqDto.setTel("01011112222");

        // stub 1
        User userA = newMockUser(1L, "userA", "유저A");
        Account userAAccount1 = newMockAccount(1L, 1111L, 1000L, userA);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(userAAccount1)); // service 호출 후 실행

        // stub 2 (stub이 진행될 때마다 연관된 객체는 새로 만들어서 주입해야 한다. -> 타이밍 때문에 꼬인다.)
        User userB = newMockUser(1L, "userA", "유저A");
        Account userAAccount2 = newMockAccount(1L, 1111L, 1000L, userB);
        Transaction transaction = newMockDepositTransaction(1L, userAAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction); // service 호출 후 실행

        //when
        AccountDepositRespDto accountDepositRespDto = accountService.deposit(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositRespDto);
        log.info("테스트 : {}", responseBody);

        //then
        assertThat(userAAccount1.getBalance()).isEqualTo(1100L);
    }

    @Test
    void account_deposit_fail_0원이하() {
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(0L);
        accountDepositReqDto.setCategory(TransactionCategory.DEPOSIT.getValue());
        accountDepositReqDto.setTel("01011112222");

        //when
        String customApiException = assertThrows(CustomApiException.class, () -> accountService.deposit(accountDepositReqDto)).getMessage();

        //then
        String error = "0원 이하의 금액을 입금할 수 없습니다.";
        assertThat(customApiException).isEqualTo(error);
    }

    @Test
    void account_deposit_fail_계좌찾을수없음() {
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(4444L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setCategory("DEPOSIT");
        accountDepositReqDto.setTel("01011112222");

        //when
        String customApiException = assertThrows(CustomApiException.class, () -> accountService.deposit(accountDepositReqDto)).getMessage();

        //then
        String error = "계좌를 찾을 수 없습니다.";
        assertThat(customApiException).isEqualTo(error);
    }

    /**
     * 서비스 테스트는 무엇인가??
     * DTO를 만드는 책임 -> 서비스에 있지만, Controller에서 테스트 할것이기때문에 안해도 된다.
     * DB 관련된 로직 -> 서비스 테스트에서 할 것이 아니다.
     * DB 관련된 것을 조회했을 때, 그 값을 통해서 어떤 비지니스 로직이 흘러가는 것이 있으면 -> stub으로 정의해서 테스트 해보면 된다!
     * (가짜로 DB 스텁을 만들어서 검증)
     */
    @Test
    void deposit_test3() {
        //given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;
        //when
        if(amount <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }
        account.deposit(amount);

        //then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }


    @Test
    void withdraw_test() {
        // given
        Long amount = 100L;
        Long password = 1234L;
        Long userId = 1L;

        User userA = newMockUser(1L, "userA", "유저A");
        Account userAAccount = newMockAccount(1L, 1111L, 1000L, userA);

        //when
        // 0원 체크
        if(amount <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }
        // 소유자 확인
        userAAccount.checkOwner(userId);
        // 패스워드 확인
        userAAccount.checkPassword(password);
        // 잔액 확인
//        userAAccount.checkBalance(amount);
        // 출금
        userAAccount.withdraw(amount);

        //then
        assertThat(userAAccount.getBalance()).isEqualTo(900L);
    }

    @Test
    void transfer_test() {
        Long userId = 1L;
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setCategory("TRANSFER");

        User userA = newMockUser(1L, "userA", "유저A");
        User userB = newMockUser(2L, "userB", "유저B");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, userA);
        Account depositAccount = newMockAccount(1L, 1111L, 1000L, userB);

        //when
        // 출금계좌 != 입금계좌
        if(accountTransferReqDto.getWithdrawNumber().longValue() == accountTransferReqDto.getDepositNumber().longValue()){
            throw new CustomApiException("입출금계좌가 동일할 수 없습니다.");
        }

        // 0원 체크
        if(accountTransferReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }
        // 출금 소유자 확인 (로그인한 사람과 동일한지)
        withdrawAccount.checkOwner(userId);
        // 출금계좌 비밀번호 확인
        withdrawAccount.checkPassword(accountTransferReqDto.getWithdrawPassword());
        // 출금계좌 잔액 확인
        withdrawAccount.checkBalance(accountTransferReqDto.getAmount());
        // 이체하기
        withdrawAccount.withdraw(accountTransferReqDto.getAmount());
        depositAccount.deposit(accountTransferReqDto.getAmount());

        //then
        assertThat(withdrawAccount.getBalance()).isEqualTo(900L);
        assertThat(depositAccount.getBalance()).isEqualTo(1100L);
    }
}