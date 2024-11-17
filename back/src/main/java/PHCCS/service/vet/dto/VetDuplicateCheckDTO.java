package PHCCS.service.vet.dto;

public class VetDuplicateCheckDTO {
    private boolean emailDuplicate;
    private boolean nickNameDuplicate;
    private boolean phoNoDuplicate;
    private boolean licenseNoDuplicate;

    // 생성자
    public VetDuplicateCheckDTO(boolean emailDuplicate, boolean nickNameDuplicate, boolean phoNoDuplicate, boolean licenseNoDuplicate) {
        this.emailDuplicate = emailDuplicate;
        this.nickNameDuplicate = nickNameDuplicate;
        this.phoNoDuplicate = phoNoDuplicate;
        this.licenseNoDuplicate = licenseNoDuplicate;
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

    // 라이센스 번호 중복 여부
    public boolean isLicenseNoDuplicate() { return licenseNoDuplicate; }

    // 어떤 항목이라도 중복되는지 확인
    public boolean isAnyDuplicate() {
        return emailDuplicate || nickNameDuplicate || phoNoDuplicate || licenseNoDuplicate;
    }
}

