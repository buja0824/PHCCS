package PHCCS.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestEx extends CommonEx {
    public BadRequestEx(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
