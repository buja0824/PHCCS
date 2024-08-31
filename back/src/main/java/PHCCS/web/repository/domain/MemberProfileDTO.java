package PHCCS.web.repository.domain;

import lombok.Data;

@Data
public class MemberProfileDTO {

    String email;
    String pwd;
    String name;
    String nickName;
    String phoNo;

}
