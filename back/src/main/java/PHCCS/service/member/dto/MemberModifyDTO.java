package PHCCS.service.member.dto;

import lombok.Data;

@Data
public class MemberModifyDTO {
    private String currentPwd;
    private String pwd;
    private String name;
    private String nickname;
    private String phoNo;
}
