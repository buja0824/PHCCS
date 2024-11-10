package PHCCS.service.member.dto;

public class DuplicateCheckDTO {
    private boolean emailDuplicate;
    private boolean nickNameDuplicate;
    private boolean phoNoDuplicate;

    // 생성자
    public DuplicateCheckDTO(boolean emailDuplicate, boolean nickNameDuplicate, boolean phoNoDuplicate) {
        this.emailDuplicate = emailDuplicate;
        this.nickNameDuplicate = nickNameDuplicate;
        this.phoNoDuplicate = phoNoDuplicate;
    }

    // 이메일 중복 여부
    public boolean isEmailDuplicate() {
        return emailDuplicate;
    }

    // 닉네임 중복 여부
    public boolean isNickNameDuplicate() {
        return nickNameDuplicate;
    }

    // 전화번호 중복 여부
    public boolean isPhoNoDuplicate() {
        return phoNoDuplicate;
    }

    // 어떤 항목이라도 중복되는지 확인
    public boolean isAnyDuplicate() {
        return emailDuplicate || nickNameDuplicate || phoNoDuplicate;
    }
}
