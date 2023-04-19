package com.side.workout.handler.aop;

import com.side.workout.handler.ex.CustomValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component // Service, Controller, Repository 등이 아니기때문에 Ioc 컨테이너에 등록해야 합니다.
@Aspect
public class CustomValidationAdvice {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){

    }
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){

    }

    @Around("postMapping() || putMapping()") // Around는 joinpoint의 전후 제어가 가능합니다.
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 매개변수들을 받아옵니다.
        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            if(arg instanceof BindingResult){
                BindingResult bindingResult = (BindingResult) arg; // 다운캐스팅을 합니다.

                if(bindingResult.hasErrors()){
                    Map<String, String> errorMap = new HashMap<>();
                    // 유효성 검사를 통과 못한 에러를 담기
                    for (FieldError error : bindingResult.getFieldErrors()){
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    throw new CustomValidationException("유효성검사 실패", errorMap);
                }
            }
        }
        return proceedingJoinPoint.proceed(); // 정상적으로 해당 메서드 실행
    }
}
