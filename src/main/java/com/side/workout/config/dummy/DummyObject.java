package com.side.workout.config.dummy;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.domain.transaction.TransactionCategory;
import com.side.workout.domain.user.User;
import com.side.workout.domain.user.UserEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyObject {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    protected Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository){
        account.withdraw(100L); // 1000 -> 900원이 된다.
        // Repository Test 에서는 더티체킹 됨
        // Controller Test 에서는 더티체킹 안됨
        if (accountRepository != null) {
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(account)
                .depositAccount(null)
                .withdrawAccountBalance(account.getBalance())
                .depositAccountBalance(null)
                .amount(100L)
                .transaction_category(TransactionCategory.WITHDRAW)
                .sender(account.getAccountNumber()+"")
                .receiver("ATM")
                .build();
        return transaction;
    }
    protected Transaction newDepositTransaction(Account account, AccountRepository accountRepository){
        account.deposit(100L); // 1000 -> 900원이 된다.
        // 더티체킹이 안되기 때문에
        if (accountRepository != null) {
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(account)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .transaction_category(TransactionCategory.DEPOSIT)
                .sender("ATM")
                .receiver(account.getAccountNumber()+"")
                .tel("01011112222")
                .build();
        return transaction;
    }

    protected Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount, AccountRepository accountRepository){
        withdrawAccount.withdraw(100L);
        depositAccount.deposit(100L);
        // 더티체킹이 안되기 때문에
        if (accountRepository != null) {
            accountRepository.save(withdrawAccount);
            accountRepository.save(depositAccount);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .withdrawAccountBalance(withdrawAccount.getBalance())
                .depositAccountBalance(depositAccount.getBalance())
                .amount(100L)
                .transaction_category(TransactionCategory.TRANSFER)
                .sender(withdrawAccount.getAccountNumber()+"")
                .receiver(depositAccount.getAccountNumber()+"")
                .build();
        return transaction;
    }

    // 계좌 1111L 1000원
    // 입금 트랜잭션 -> 계좌 1100원 변경 -> 입금 트랜잭션 히스토리가 생성되어야 함.
    protected static Transaction newMockDepositTransaction(Long id, Account account) {
        account.deposit(100L);
        Transaction transaction = Transaction.builder()
                .id(id)
                .withdrawAccountBalance(null)
                .depositAccount(account)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .transaction_category(TransactionCategory.DEPOSIT)
                .sender("ATM")
                .receiver(account.getAccountNumber()+"")
                .tel("01011112222")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
        return transaction;
    }

    // Entity save 용도
    protected User newUser(String username, String fullname){
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username+"@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    // 가짜 stub -> when.thenReturn 구조에서 사용
    protected User newMockUser(Long id, String username, String fullname){
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username+"@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }
    protected Account newAccount(Long number, User user){
        return Account.builder()
                .accountNumber(number)
                .password(1234L)
                .balance(1000L)
                .users(user)
                .build();
    }
    protected Account newMockAccount(Long id, Long number, Long balance, User user){
        return Account.builder()
                .id(id)
                .accountNumber(number)
                .password(1234L)
                .balance(balance)
                .users(user)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }
}
