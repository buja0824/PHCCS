package PHCCS.common.exception;

import org.springframework.http.HttpStatus;

public class InternalServerEx extends CommonEx {
    public InternalServerEx(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
