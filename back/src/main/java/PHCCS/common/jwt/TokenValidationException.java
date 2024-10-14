package PHCCS.common.jwt;

// 커스텀 예외 클래스 정의
public class TokenValidationException extends RuntimeException {
    private final TokenStatus status;

    // 생성자: 예외 메시지와 상태를 받음
    public TokenValidationException(String message, TokenStatus status) {
        super(message);
        this.status = status;
    }

    // 상태를 반환하는 getter 메서드
    public TokenStatus getStatus() {
        return status;
    }
}
