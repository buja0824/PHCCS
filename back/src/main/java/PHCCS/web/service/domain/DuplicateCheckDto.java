package PHCCS.web.service.domain;

public class DuplicateCheckDto {
    private boolean emailDuplicate;
    private boolean nicknameDuplicate;
    private boolean phoNoDuplicate;

    // 생성자
    public DuplicateCheckDto(boolean emailDuplicate, boolean nicknameDuplicate, boolean phoNoDuplicate) {
        this.emailDuplicate = emailDuplicate;
        this.nicknameDuplicate = nicknameDuplicate;
        this.phoNoDuplicate = phoNoDuplicate;
    }

    // Getter 메서드
    public boolean isEmailDuplicate() {
        return emailDuplicate;
    }

    public boolean isNicknameDuplicate() {
        return nicknameDuplicate;
    }

    public boolean isPhoNoDuplicate() {
        return phoNoDuplicate;
    }
}
