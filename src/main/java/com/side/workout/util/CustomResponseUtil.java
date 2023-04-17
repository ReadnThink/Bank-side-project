package com.side.workout.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomResponseUtil {
    public static void unAuthentication(HttpServletResponse response, String msg) {
        try {
            ObjectMapper om = new ObjectMapper(); // Json을 만들기 위해 ObjectMapper 선언
            ResponseDto<?> responseDto = new ResponseDto<>(-1, msg, null);
            String responseBody = om.writeValueAsString(responseDto); // ResopneseDto 객체 -> Json으로 바꿔줍니다.
            response.setContentType("application/json; charset=utf-8"); // json으로 응답
            response.setStatus(401);
            response.getWriter().println(responseBody); // Json으로 바뀐 responseDto를 반환합니다.
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }

    public static void unAuthorization(HttpServletResponse response, String msg) {
        try {
            ObjectMapper om = new ObjectMapper(); // Json을 만들기 위해 ObjectMapper 선언
            ResponseDto<?> responseDto = new ResponseDto<>(-1, msg, null);
            String responseBody = om.writeValueAsString(responseDto); // ResopneseDto 객체 -> Json으로 바꿔줍니다.
            response.setContentType("application/json; charset=utf-8"); // json으로 응답
            response.setStatus(403);
            response.getWriter().println(responseBody); // Json으로 바뀐 responseDto를 반환합니다.
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }
}
