package PHCCS.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;


    public String createAccessToken(Long id, int role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(claims, id.toString(), jwtProperties.getAccessTokenExpiration());
    }

    public String createRefreshToken(Long id){
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, id.toString(), jwtProperties.getRefreshTokenExpiration());
    }

    public Boolean validateToken(String token){
        try{
            extractAllClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public String extractId(String token){
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, (jwtProperties.getSecretKey()+jwtProperties.getPasswordSalt()).getBytes())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey()+jwtProperties.getPasswordSalt().getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
