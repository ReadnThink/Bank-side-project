package com.side.workout.temp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

@Slf4j
public class RegexTest {
    @Test
    void 한글_정규표현식() {
        String value ="한글만가능합니다";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void 한글_X() {
        String value ="noKorean";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void 영어만() {
        String value ="OnlyEnglish";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void 영어X() {
        String value ="영어안됩니다";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void 영어_숫자() {
        String value ="EnglishAndDigit123";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void 영어만_길이2_5() {
        String value ="limit";
        boolean result = Pattern.matches("^[a-zA-Z]{2,5}$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void user_username_test() {
        String value ="test123";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void user_fullname_test() {
        String value ="테스트test";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void user_email_test() {
        String value ="test@nate.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z0-9]{2,3}$", value);
        log.info("테스트 : {}",result);
    }

    @Test
    void account_category_test1() {
        String category ="DEPOSIT";
        boolean result = Pattern.matches("^(DEPOSIT)$", category);
        log.info("테스트 : {}",result);
    }

    @Test
    void account_category_test2() {
        String category ="TRANSFER";
        boolean result = Pattern.matches("^(DEPOSIT|TRANSFER)$", category);
        log.info("테스트 : {}",result);
    }

    @Test
    void account_tel_test1() {
        String tel ="010-1234-1234";
        boolean result = Pattern.matches("^[0-9]{3}-[0-9]{4}-[0-9]{4}$", tel);
        log.info("테스트 : {}",result);
    }

    @Test
    void account_tel_test2() {
        String tel ="01011112222";
        boolean result = Pattern.matches("^[0-9]{11}", tel);
        log.info("테스트 : {}",result);
    }
}
