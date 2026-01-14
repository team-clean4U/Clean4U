package org.example.clean4u._core.errors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.clean4u._core.errors.exception.*;
import org.example.clean4u._core.response.ApiErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("== 입력값 오류 발생 ==");
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(400, message));
    }

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<ApiErrorResponse> exception400(Exception400 e) {
        log.warn("== 400 예외 발생 ==");
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(400, e.getMessage()));
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<ApiErrorResponse> exception401(Exception401 e) {
        log.warn("== 401 예외 발생 ==");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(401, e.getMessage()));
    }

    @ExceptionHandler(Exception403.class)
    public ResponseEntity<ApiErrorResponse> exception403(Exception403 e) {
        log.warn("== 403 예외 발생 ==");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(403, e.getMessage()));
    }

    @ExceptionHandler(Exception404.class)
    public ResponseEntity<ApiErrorResponse> exception404(Exception404 e) {
        log.warn("== 404 예외 발생 ==");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(404, e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("== 데이터베이스 제약조건 위반 오류 발생 ==");
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        String message = "데이터베이스 제약 조건 위반입니다.";
        if(e.getMessage() != null && e.getMessage().contains("FOREIGN KEY")) {
            message = "관련된 데이터가 있어 삭제할 수 없습니다.";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(409, message));
    }

    @ExceptionHandler(Exception500.class)
    public ResponseEntity<ApiErrorResponse> exception500(Exception500 e) {
        log.warn("== 500 예외 발생 ==");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(500, e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException e) {
        log.warn("== 예상하지 못한 에러 발생 ==");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(500, "서버 내부 오류가 발생했습니다."));
    }
}
