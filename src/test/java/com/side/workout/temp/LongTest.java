package com.side.workout.temp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LongTest {
    @Test
    void long_test() {
        //given
        Long num1 = 1111L;
        Long num2 = 1111L;

        //when
        if(num1 == num2){
            log.info("테스트 : 동일합니다.");
        }else{
            log.info("테스트 : 동일하지 않습니다.");
        }

        if(num1.longValue() == num2.longValue()){
            log.info("테스트2 : 동일합니다.");
        }else{
            log.info("테스트2 : 동일하지 않습니다.");
        }

        /**
         * 대소 구분은 .longValue() 필요없다
         */
        Long amount1 = 100L;
        Long amount2 = 1000L;

        //when
        if(amount1 < amount2){
            log.info("테스트 : amount2가 더 큽니다");
        }else{
            log.info("테스트 : amount2가 더 작습니다");
        }

        if(amount1.longValue() < amount2.longValue()){
            log.info("테스트 : amount2가 더 큽니다");
        }else{
            log.info("테스트 : amount2가 더 작습니다");
        }

    }
}
