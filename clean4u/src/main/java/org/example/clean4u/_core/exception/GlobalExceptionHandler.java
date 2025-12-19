package org.example.clean4u._core.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<String> exception400(Exception400 e, HttpServletRequest request) {
        log.warn("== 400 에러 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        String script = "<script>alert('" + e.getMessage() + "');" +
                "history.back();" +
                "</script>";

        request.setAttribute("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<String> exception401(Exception401 e, HttpServletRequest request) {
        String script = "<script>alert('" + e.getMessage() + "');" +
                "location.href = '/login';" +
                "</script>";

        request.setAttribute("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }
}
