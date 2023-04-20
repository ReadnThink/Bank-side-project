package com.side.workout.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.workout.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CustomResponseUtil {

    public static void success(HttpServletResponse response, Object dto) {
        try {
            ObjectMapper om = new ObjectMapper(); // Json을 만들기 위해 ObjectMapper 선언
            ResponseDto<?> responseDto = new ResponseDto<>(1, "로그인 성공", dto);
            String responseBody = om.writeValueAsString(responseDto); // ResopneseDto 객체 -> Json으로 바꿔줍니다.
            response.setContentType("application/json; charset=utf-8"); // json으로 응답
            response.setStatus(200);
            response.getWriter().println(responseBody); // Json으로 바뀐 responseDto를 반환합니다.
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }
    public static void fail(HttpServletResponse response, String msg, HttpStatus httpStatus) {
        try {
            ObjectMapper om = new ObjectMapper(); // Json을 만들기 위해 ObjectMapper 선언
            ResponseDto<?> responseDto = new ResponseDto<>(-1, msg, null);
            String responseBody = om.writeValueAsString(responseDto); // ResopneseDto 객체 -> Json으로 바꿔줍니다.
            response.setContentType("application/json; charset=utf-8"); // json으로 응답
            response.setStatus(httpStatus.value());
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
