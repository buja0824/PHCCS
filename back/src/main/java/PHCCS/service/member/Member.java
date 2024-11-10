package PHCCS.service.member;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 아직 멤버에 관한 코드를 작성하지 않아서
 * 다른 코드를 작성할 때 오류 없게 할려고 임시로 만든 클래스 입니다.
 * 진구가 멤버에 대한 코드를 완성하면 합쳐서 사용하면 될 듯 하오
  */
@Data
@NoArgsConstructor
public class Member {
    private Long id;

    private String email;
    private String pwd;
    private String name;
    private String nickName;
    private String phoNo;
    private LocalDate Created;
    private int role;

}
