package PHCCS.common.exception;

import org.springframework.http.HttpStatus;

public class InternalServerEx extends CommonEx{

    public InternalServerEx(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
