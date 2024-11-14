package PHCCS.common.exception;

import PHCCS.service.member.exception.LoginFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    // 로그인 실패 처리 (401 Unauthorized)
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<String> handlerEx(LoginFailedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("아이디 또는 비밀번호가 잘못되었습니다.");
    }

    // 잘못된 요청 처리 (BadRequestEx)
    @ExceptionHandler
    public ResponseEntity<String> handlerEx(BadRequestEx e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(e.getMessage());
    }

    // 내부 서버 오류 처리 (InternalServerEx)
    @ExceptionHandler
    public ResponseEntity<String> handlerEx(InternalServerEx e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(e.getMessage());
    }

    // 기타 예외 처리 (500 Internal Server Error)
    @ExceptionHandler
    public ResponseEntity<String> handlerEx(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("시스템 오류가 발생했습니다.");
    }
}