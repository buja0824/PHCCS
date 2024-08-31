package PHCCS.web.repository.domain;

import lombok.Data;

@Data
public class MemberModifyDto {
    private String pwd;
    private String name;
    private String nickName;
    private String phoNo;
}
