package PHCCS.memberservice.domain;

import lombok.Data;

@Data
public class Member {

    private Long id;

    private String email;
    private String password;

    private String name;
    private String nickname;
    private String phoNo;
    private String address;
    
}
