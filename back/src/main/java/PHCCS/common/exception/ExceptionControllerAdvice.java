package PHCCS.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<String> handlerEx(CommonEx e){
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String>handlerEx(Exception e){
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body("시스템 오류가 발생했습니다.");
    }

}
