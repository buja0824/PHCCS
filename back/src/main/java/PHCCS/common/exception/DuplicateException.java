package PHCCS.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateException extends CommonEx {
  public DuplicateException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}