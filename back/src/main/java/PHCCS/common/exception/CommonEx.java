package PHCCS.common.exception;

import org.springframework.http.HttpStatus;

public class CommonEx extends RuntimeException{
    private final HttpStatus httpStatus;

    public CommonEx(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public final HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
