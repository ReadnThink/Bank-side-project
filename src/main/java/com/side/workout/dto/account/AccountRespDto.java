package com.side.workout.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.side.workout.domain.account.Account;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.domain.user.User;
import com.side.workout.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {

    @Setter
    @Getter
    public static class AccountTransferRespDto{
        private Long id; // 계좌 id
        private Long number; // 계좌번호
        private Long balance; // 출금계좌잔액
        private TransactionDto transaction;

        public AccountTransferRespDto(Account account, Transaction trancaction) {
            this.id = account.getId();
            this.number = account.getAccountNumber();
            this.balance = account.getBalance();
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
            @JsonIgnore
            private Long depositAccountBalance; // 입금계좌 잔액 테스트 확인용
            private String createAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.category = transaction.getTransaction_category().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreateAt());
            }
        }
    }

    // DTO가 똑같아도 재소용 하지 않기 (나중에 DTO 달라져야 할때 DTO를 공유하면 피곤해진다.)
    @Setter
    @Getter
    public static class AccountWithdrawRespDto{
        private Long id; // 계좌 id
        private Long number; // 계좌번호
        private Long balance; // 잔액
        private TransactionDto transaction;

        public AccountWithdrawRespDto(Account account, Transaction trancaction) {
            this.id = account.getId();
            this.number = account.getAccountNumber();
            this.balance = account.getBalance();
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
            private String createAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.category = transaction.getTransaction_category().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreateAt());
            }
        }
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
            @JsonIgnore // 클라이언트에게 전달x -> 컨트롤러에서 테스트 용도
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
    @Setter
    @Getter
    public static class  AccountCreateRespDto{
        private Long id;
        private Long number;
        private Long balance;

        public AccountCreateRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getAccountNumber();
            this.balance = account.getBalance();
        }
    }

    @Getter
    @Setter
    public static class AccountListRespDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
//            this.accounts = accounts.stream().map((account) -> new AccountDto(account)).collect(Collectors.toList());
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Getter
        @Setter
        private class AccountDto{
            private Long id;
            private Long number;
            private Long balance;
            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getAccountNumber();
                this.balance = account.getBalance();
            }
        }
    }

}
