package PHCCS.domain;


import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RefreshToken {

    /**
    private BigInteger id;
    private String subject; // 멤버 id
    private String issuer;
    private String claims;

    private LocalDateTime issuedAt;
    private LocalDateTime expiration;
     */
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
