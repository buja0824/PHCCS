package PHCCS.web.repository.domain;

import lombok.Data;

@Data
public class SessionMemberDTO {
    private String email;
    private String pwd;
    private int role;

    public SessionMemberDTO(String email, String pwd, int role) {
        this.email = email;
        this.pwd = pwd;
        this.role = role;
    }
}
