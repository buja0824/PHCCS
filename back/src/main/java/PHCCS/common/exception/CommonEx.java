package PHCCS.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
abstract class CommonEx extends RuntimeException{

    private final HttpStatus httpStatus;
    public CommonEx(HttpStatus httpStatus, String message){
        super(message);
        this.httpStatus = httpStatus;
    }
}