package PHCCS.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration  // @Component 대신 사용
@ConfigurationProperties(prefix = "jwt")

public class JwtProperties {

    private String issuer;
    private String secretKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

    // Setters
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Getters
    public String getIssuer() {
        return issuer;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public long  getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
