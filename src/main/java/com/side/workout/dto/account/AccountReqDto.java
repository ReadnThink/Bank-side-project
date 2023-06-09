package com.side.workout.dto.account;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AccountReqDto {

    @Setter
    @Getter
    public static class AccountTransferReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 10)
        private Long withdrawNumber;
        @NotNull
        @Digits(integer = 4, fraction = 10)
        private Long depositNumber;
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long withdrawPassword;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "TRANSFER")
        private String category;

    }
    @Setter
    @Getter
    public static class AccountWithdrawReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 10)
        private Long number;
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "WITHDRAW")
        private String category;

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
    @Getter
    @Setter
    public static class AccountCreateReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 10)
        private Long number;
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user){
            return Account.builder()
                    .accountNumber(number)
                    .password(password)
                    .balance(1000L)
                    .users(user)
                    .build();
        }
    }
}
