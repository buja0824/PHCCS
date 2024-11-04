package PHCCS.common.exception;


import org.springframework.http.HttpStatus;

public class BadRequestEx extends CommonEx{

    public BadRequestEx(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
