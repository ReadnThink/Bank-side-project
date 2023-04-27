package com.side.workout.dto.transaction;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class transactionRespDto {

    @Setter
    @Getter
    public static class TransactionListRespDto {
        private List<TransactionDto> transactions = new ArrayList<>();

        public TransactionListRespDto(List<Transaction> transactions, Account account) {
            this.transactions = transactions.stream()
                    .map((transaction)-> new TransactionDto(transaction, account.getAccountNumber()))
                    .collect(Collectors.toList());
        }

        @Setter
        @Getter
        public class TransactionDto {
            private Long id;
            private String category;
            private Long amount;
            private String sender;
            private String receiver;
            private String tel;
            private String createdAt;
            private Long balance;

            public TransactionDto(Transaction transaction, Long accountNumber) { // accountNumber -> 잔액 계좌를 결정하기 위해
                this.id = transaction.getId();
                this.category = transaction.getTransaction_category().getValue();
                this.amount = transaction.getAmount();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreateAt());
                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

                // 입출금 계좌 확인
                if(transaction.getDepositAccount() == null){
                    this.balance = transaction.getWithdrawAccountBalance();
                }else if(transaction.getWithdrawAccount() == null){
                    this.balance = transaction.getDepositAccountBalance();
                }else {
                    // 둘 다 있는 경우
                    if(accountNumber.equals(transaction.getDepositAccount().getAccountNumber())){
                        // 입금계좌와 동일하다면
                        this.balance = transaction.getDepositAccountBalance();
                    }else{
                        // 입금계좌와 다르면
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}
