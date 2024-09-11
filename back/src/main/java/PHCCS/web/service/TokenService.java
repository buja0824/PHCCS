package PHCCS.web.service;

import PHCCS.domain.RefreshToken;
import PHCCS.jwt.JwtProperties;
import PHCCS.jwt.JwtUtil;
import PHCCS.web.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public void storeRefreshToken(String refreshToken) {
        tokenRepository.saveRefreshToken(refreshToken);
    }

    public boolean removeRefreshToken(String refreshToken){
        return tokenRepository.removeRefreshToken(refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        String storedRefreshToken = tokenRepository.getRefreshTokenByToken(refreshToken);
        // 받은 토큰과 일치하는 토큰이 없을때
        if(storedRefreshToken == null){
            return null;
        }
        // 저장된 토큰이 기간이 만료되었을때
        if(!jwtUtil.isTokenExpired(refreshToken)){
            // 만료된 토큰을 삭제
            removeRefreshToken(refreshToken);
            return null;
        }

        //새로운 accessToken 반환
        return jwtUtil.createAccessToken(Long.parseLong(jwtUtil.extractSubject(storedRefreshToken)), jwtUtil.extractRole(storedRefreshToken));

    }

}