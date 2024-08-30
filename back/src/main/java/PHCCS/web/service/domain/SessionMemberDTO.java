package PHCCS.web.service.domain;

import lombok.Data;

@Data
public class SessionMemberDTO {
    private Long id;
    private String email;
    private String pwd;
    private int role;

    public SessionMemberDTO(){}

    public SessionMemberDTO(Long id, String email, String pwd, int role) {
        this.id = id;
        this.email = email;
        this.pwd = pwd;
        this.role = role;
    }
}
