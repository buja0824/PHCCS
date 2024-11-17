package PHCCS.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CommonEx {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
