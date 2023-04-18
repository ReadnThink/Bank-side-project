package com.side.workout.domain.handler.ex;

public class CustomApiException extends RuntimeException{
    public CustomApiException(String message){
        super(message);
    }
}
