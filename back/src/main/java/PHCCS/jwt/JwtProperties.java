package PHCCS.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
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
        log.info("accessTokenExpiration: {}", accessTokenExpiration);
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        log.info("refreshTokenExpiration: {}", refreshTokenExpiration);
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
