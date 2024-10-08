package PHCCS.jwt;

import PHCCS.web.repository.TokenRepository;
import PHCCS.web.service.TokenService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil {

    private final TokenRepository tokenRepository;
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

    public TokenStatus validateAccessToken(String token){
        try {
            extractAllClaims(token); // JWT 파싱 및 유효성 검사
            return TokenStatus.VALID; // 유효한 토큰일 경우
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다: {}", e.getMessage());
            return TokenStatus.EXPIRED; // 만료된 토큰
        } catch (SignatureException e) {
            log.info("서명이 잘못된 토큰입니다: {}", e.getMessage());
            return TokenStatus.INVALID_SIGNATURE; // 서명이 잘못된 토큰
        } catch (MalformedJwtException e) {
            log.info("잘못된 형식의 토큰입니다: {}", e.getMessage());
            return TokenStatus.MALFORMED; // 형식이 잘못된 토큰
        } catch (Exception e) {
            log.info("알 수 없는 오류로 인해 토큰 검증에 실패했습니다: {}", e.getMessage());
            return TokenStatus.UNKNOWN_ERROR; // 기타 모든 예외에 대한 처리
        }
    }

    public TokenStatus validateRefreshToken(String token){
        try {
            // 토큰의 기본 유효성 검증 (서명 확인)
           extractAllClaims(token);

            // 서버에 저장된 리프레시 토큰과 일치하는지 확인
            String storedRefreshToken = tokenRepository.getRefreshTokenByToken(extractId(token), token);
            log.info("storedRefreshToken: {}", storedRefreshToken);
            if (storedRefreshToken == null || !storedRefreshToken.equals(actual(token))) {
                return TokenStatus.INVALID;
            }

            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (SignatureException e) {
            return TokenStatus.INVALID_SIGNATURE;
        } catch (MalformedJwtException e) {
            return TokenStatus.MALFORMED;
        } catch (Exception e) {
            return TokenStatus.UNKNOWN_ERROR;
        }
    }


    public Integer extractRole(String token){
        return (Integer) extractAllClaims(token).get("role");
    }

    // token에서 memberId를 추출할때 사용
    public Long extractSubject(String token){
        return Long.parseLong(extractAllClaims(token).getSubject());
    }

    public static String actual(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // 'Bearer ' 이후의 순수한 JWT 반환
        }
        return token;
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

    // token에서 id를 추출할때 사용(id는 토큰 고유의 식별 id를 의미, memberId X)
    public String extractId(String token){
        log.info("extractId에서 토큰값: {}", token);
        Claims claims = extractAllClaims(token);
        String jti = claims.getId();
        log.info("추출된 jti: {}", jti);
        return jti;
    }

    private byte[] getSigningKey(String secret) {
        // Base64 URL-safe 디코딩
        return Base64.getUrlDecoder().decode(secret);
    }


    private String createToken(Map<String, Object> claims, String subject, long expirationTime){
        long currentTimeMillis = System.currentTimeMillis();
        log.info("토큰 생성 시간: {}", new Date(currentTimeMillis));
        log.info("토큰 만료 시간: {}", new Date(currentTimeMillis + expirationTime));
        String jti = UUID.randomUUID().toString();
        log.info("생성된 UUID: {}", jti);
        return Jwts.builder()
                .setClaims(claims)
                .setId(jti)
                .setSubject(subject)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, getSigningKey(jwtProperties.getSecretKey()))
                .compact();
    }

    private Claims extractAllClaims(String token) {
        log.info("extractallclaims 에서 받은 토큰:{}", token);
        log.info("extractallclaims에서 받은 추출을 위한 리프레시 토큰:{}", actual(token));
        String actualToken = actual(token);
        log.info("actualToken: {}", actualToken);
        log.info("signkey: {}", getSigningKey(jwtProperties.getSecretKey()));
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(jwtProperties.getSecretKey()))
                .build()
                .parseClaimsJws(actualToken)
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
