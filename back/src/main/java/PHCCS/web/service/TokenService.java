package PHCCS.web.service;


import PHCCS.jwt.JwtUtil;
import PHCCS.web.repository.MemberRepository;
import PHCCS.web.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void storeRefreshToken(String refreshToken) {
        tokenRepository.saveRefreshToken(refreshToken);
    }

    public boolean removeRefreshToken(String refreshToken){
        return tokenRepository.removeRefreshToken(refreshToken);
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        String storedRefreshToken = tokenRepository.getRefreshTokenByToken(refreshToken);
        // [LOG] 저장된 refreshToken
        log.info("storedTokens: {}", storedRefreshToken);
        // 받은 토큰과 일치하는 토큰이 없을때
        if(storedRefreshToken == null){
            return null;
        }
        // 저장된 토큰 만료 확인
        boolean isSuccess = jwtUtil.isTokenExpired(refreshToken);

        if(isSuccess){
            // 만료된 토큰을 삭제
            removeRefreshToken(refreshToken);
            return null;
        }

        Map<String, String> tokens = new HashMap<>();

        Long id = Long.parseLong(jwtUtil.extractSubject(storedRefreshToken));
        String newAccessToken = jwtUtil.createAccessToken(id, memberRepository.findRoleById(id));
        log.info("새로운 accessToken: {}", newAccessToken);
        log.info("새로운 accessToken 만료시간: {}", jwtUtil.extractExpiration(newAccessToken));
        tokens.put("accessToken", newAccessToken);


        tokens.put("refreshToken", refreshToken);

        // [LOG] 새로 생성된 accessToken, refreshToken
        log.info("new tokens: {}", tokens);

        return tokens;
    }

}