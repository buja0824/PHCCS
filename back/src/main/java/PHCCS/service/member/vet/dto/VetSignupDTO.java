package PHCCS.service.member.vet.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VetSignupDTO {
    // member 정보
    private Long id;
    private String email;
    private String pwd;
    private String name;
    private String nickName;
    private String phoNo;
    private int role;
    // 요청 정보
    private String licenseNo;
    private String hospitalName;
    private String hospitalAddr;
    private String requestDate;
}
