package org.example.clean4u._core.errors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.clean4u._core.errors.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Getter
    @AllArgsConstructor
    public static class ApiErrorResponse {
        private final int status;
        private final String message;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("== 입력값 오류 발생 ==");
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");

        if (isApiRequest(request)) {
            return jsonError(HttpStatus.BAD_REQUEST, message);
        }
        return alertBack(message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<?> exception400(Exception400 e, HttpServletRequest request) {
        log.warn("== 400 예외 발생 ==");
        if (isApiRequest(request)) {
            return jsonError(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return alertBack(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<?> exception401(Exception401 e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return jsonError(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        String script = "<script>alert('" + e.getMessage() + "');" +
                "location.href = '/login';" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Exception403.class)
    public ResponseEntity<?> exception403(Exception403 e, HttpServletRequest request) {
        log.warn("== 403 예외 발생 ==");
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());
        if (isApiRequest(request)) {
            return jsonError(HttpStatus.FORBIDDEN, e.getMessage());
        }
        return alertBack(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(Exception404.class)
    public ResponseEntity<?> exception404(Exception404 e, HttpServletRequest request) {
        log.warn("== 404 예외 발생 ==");
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());
        if (isApiRequest(request)) {
            return jsonError(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return alertBack(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception500.class)
    public ResponseEntity<?> exception500(Exception500 e, HttpServletRequest request) {
        log.warn("== 500 예외 발생 ==");
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());
        if (isApiRequest(request)) {
            return jsonError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return alertBack(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("== 데이터베이스 제약조건 위반 오류 발생 ==");
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        String message = "데이터베이스 제약 조건 위반입니다.";
        if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY")) {
            message = "관련된 데이터가 있어 삭제할 수 없습니다.";
        }

        if (isApiRequest(request)) {
            return jsonError(HttpStatus.CONFLICT, message);
        }
        if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY")) {
            return alertBack(message, HttpStatus.CONFLICT, request);
        }
        return alertBack(e, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<?> handleError(Error e, HttpServletRequest request) {
        log.warn("== 클래스 로딩 오류 발생 ==");
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());
        if (isApiRequest(request)) {
            return jsonError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return alertBack(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.warn("== 예상하지 못한 에러 발생 ==");
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());
        if (isApiRequest(request)) {
            String message = e.getMessage() != null ? e.getMessage() : "예상치 못한 에러 발생";
            return jsonError(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }
        return alertBack(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/api/");
    }

    private ResponseEntity<String> alertBack(String message, HttpStatus status, HttpServletRequest request) {
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", message);

        String script = """
                    <script>
                        alert('%s');
                        history.back();
                    </script>
                """.formatted(message);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    private ResponseEntity<String> alertBack(Exception e, HttpStatus status, HttpServletRequest request) {
        return alertBack(
                e.getMessage() != null ? e.getMessage() : "예상치 못한 에러 발생",
                status,
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> jsonError(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiErrorResponse(status.value(), message));
    }
}