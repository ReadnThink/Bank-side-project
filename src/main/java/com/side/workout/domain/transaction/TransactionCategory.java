package com.side.workout.domain.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionCategory {
    WITHDRAW("출금"), DEPOSIT("입금"), TRANSFER("이체"), ALL("입출금내역");
    private String value;
}
