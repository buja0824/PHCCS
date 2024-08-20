package practice.pra.domain.repository;

import lombok.Data;

@Data
public class MemberSearchCon {
    // id 찾기시 이용
    private String name;
    private String email;

    // pwd 찾기시 이용
    private String loginId;
    private String password;
}
