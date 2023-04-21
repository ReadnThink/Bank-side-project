package com.side.workout.dto.account;

import com.side.workout.domain.account.Account;
import lombok.Getter;
import lombok.Setter;

public class AccountRespDto {
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
}
