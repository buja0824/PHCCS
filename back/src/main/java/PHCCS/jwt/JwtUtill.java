package PHCCS.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtUtill {

    @Autowired
    private JwtProperties jwtProperties;

    // 토큰 생성
    public String generateToken(String user)


}
