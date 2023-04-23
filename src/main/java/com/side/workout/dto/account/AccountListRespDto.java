package com.side.workout.dto.account;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AccountListRespDto {
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
