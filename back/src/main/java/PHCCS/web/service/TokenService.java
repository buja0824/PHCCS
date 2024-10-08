package PHCCS.web.service;


import PHCCS.domain.RefreshToken;
import PHCCS.jwt.JwtUtil;
import PHCCS.jwt.TokenStatus;
import PHCCS.jwt.TokenValidationException;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.repository.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    public void storeRefreshToken(String tokenId, String refreshToken) {
        tokenRepository.saveRefreshToken(tokenId, refreshToken);
    }

    public boolean removeRefreshToken(String tokenId, String refreshToken) {
        return tokenRepository.removeRefreshToken(tokenId, refreshToken);
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {

        // JwtUtil을 사용하여 리프레시 토큰 검증
        TokenStatus status = jwtUtil.validateRefreshToken(refreshToken);

        // 검증 결과가 VALID가 아닌 경우, 예외 발생
        if (status != TokenStatus.VALID) {
            log.info("토큰 서비스 리프레시어세스토큰체크");
            throw new TokenValidationException("리프레시 토큰이 유효하지 않습니다.", status);
        }

        // 유효한 리프레시 토큰일 경우 새로운 액세스 토큰 생성
        Long userId = jwtUtil.extractSubject(refreshToken);
        int role = memberRepository.findRoleById(userId);
        String newAccessToken = jwtUtil.createAccessToken(userId, role);

        // 액세스 토큰과 리프레시 토큰을 Map에 저장하여 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken);

        log.info("새로 발급된 AccessToken: {}", newAccessToken);
        return tokens;
    }
}