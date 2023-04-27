package com.side.workout.service;

import com.side.workout.domain.account.Account;
import com.side.workout.domain.account.AccountRepository;
import com.side.workout.domain.transaction.Transaction;
import com.side.workout.domain.transaction.TransactionRepository;
import com.side.workout.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.side.workout.dto.transaction.transactionRespDto.TransactionListRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionListRespDto getTransactionList(Long userId, Long accountNumber, String category, int page) {
        Account accountPS = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new CustomApiException("해당 계좌를 찾을 수 없습니다."));

        accountPS.checkOwner(userId);

        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountPS.getId(), category, page);
        return new TransactionListRespDto(transactionListPS, accountPS);
    }
}
