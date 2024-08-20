package practice.pra.domain.repository;


import lombok.Data;

import java.util.Date;

@Data
public class MemberUpdateDto {

    private String name;
    private String password;
    private String phoNo;     // 회원 전화번호
    private String email;       // 회원 이메일
    private String nickName;    // 닉네임
}
