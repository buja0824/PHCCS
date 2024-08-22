package practice.pra.domain;

import lombok.Data;

import java.util.Date;

@Data

public class Member {

    private Long id;

//    @NotEmpty
    private String name;        // 회원 이름
//    @NotEmpty
    private String email;       // 회원 이메일
//    @NotEmpty
    private String password;    // 회원 비밀번호
    private String phoNo;     // 회원 전화번호
    private String address; // 회원 주소
    private String nickname;
   // private Date birthdate;     // 생일

}
