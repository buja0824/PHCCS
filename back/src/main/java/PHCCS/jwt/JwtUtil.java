package PHCCS.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    @Autowired
    private final JwtProperties jwtProperties;


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

    public Integer extractRole(String token){
        return (Integer) extractAllClaims(token).get("role");
    }

    public String extractSubject(String token){
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public String extractIssuer(String token) {
        return extractAllClaims(token).getIssuer();
    }

    public Date extractIssuedAt(String token) {
        return extractAllClaims(token).getIssuedAt();
    }

    public String extractId(String token){
        return extractAllClaims(token).getId();
    }

    private byte[] getSigningKey(String secret) {
        // Base64 URL-safe 디코딩
        return Base64.getUrlDecoder().decode(secret);
    }


    private String createToken(Map<String, Object> claims, String subject, long expirationTime){
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, getSigningKey(jwtProperties.getSecretKey()))
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(jwtProperties.getSecretKey()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean compareClaims(String token1, String token2){

        boolean isSameSubject = extractSubject(token1).equals(extractSubject(token2));
        boolean isSameIssuedAt = extractIssuedAt(token1).equals(extractIssuedAt(token2));
        boolean isSameExpiration = extractExpiration(token1).equals(extractExpiration(token2));
        boolean isSameIssuer = extractIssuer(token1).equals(extractIssuer(token2));

        return isSameSubject && isSameIssuedAt && isSameExpiration && isSameIssuer;
    }

}
