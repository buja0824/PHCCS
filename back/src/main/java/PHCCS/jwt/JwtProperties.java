package PHCCS.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration  // @Component 대신 사용
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String issuer;
    private String secretKey;
    private String passwordSalt;

    // Getters
    public String getIssuer() {
        return issuer;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }
}
