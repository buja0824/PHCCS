package PHCCS.common.handler;
import PHCCS.service.member.exception.LoginFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 로그인 실패 시 예외를 잡아서 401 응답과 함께 커스텀 메시지 반환
    @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401 상태 코드 반환
    public ResponseEntity<String> handleLoginFailedException(LoginFailedException ex) {
        return new ResponseEntity<>("아이디 또는 비밀번호가 다릅니다.", HttpStatus.UNAUTHORIZED);
    }

    // 기타 예외 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>("서버에 문제가 발생했습니다. 다시 시도해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}