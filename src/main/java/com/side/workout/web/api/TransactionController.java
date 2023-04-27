package com.side.workout.web.api;

import com.side.workout.config.auth.LoginUser;
import com.side.workout.dto.ResponseDto;
import com.side.workout.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.side.workout.dto.transaction.transactionRespDto.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/s/account/{number}/transaction")
    public ResponseEntity<?> findTransactionList(@PathVariable Long number,
                                                 @RequestParam(value="category", defaultValue = "ALL") String category,
                                                 @RequestParam(value="page", defaultValue = "0") Integer page,
                                                 @AuthenticationPrincipal LoginUser loginUser){
        TransactionListRespDto transactionListRespDto = transactionService.getTransactionList(loginUser.getUser().getId(), number, category, page);
        return new ResponseEntity<>(new ResponseDto<>(1, "입출금목록보기 성공", transactionListRespDto), HttpStatus.OK);
    }
}
