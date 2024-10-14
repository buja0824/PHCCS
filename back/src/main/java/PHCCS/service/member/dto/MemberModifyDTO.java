package PHCCS.service.member.dto;

import lombok.Data;

@Data
public class MemberModifyDTO {
    private String pwd;
    private String name;
    private String nickName;
    private String phoNo;
}
