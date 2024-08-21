package practice.pra.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Member {

    private Long id;

//    @NotEmpty
    private String name;        // 회원 이름
//    @NotEmpty
    private String email;    // 회원 로그인 id(email)
//    @NotEmpty
    private String password;    // 회원 비밀번호
    private String phoNo;     // 회원 전화번호
    private String nickName;    // 닉네임
    private String address; // 주소
    private String created; // 가입일
}
