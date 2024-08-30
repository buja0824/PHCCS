package PHCCS.web.repository.domain;

import lombok.Data;

@Data
public class MemberModifyParam {
    private String pwd;
    private String name;
    private String nickName;
    private String phoNo;
}
