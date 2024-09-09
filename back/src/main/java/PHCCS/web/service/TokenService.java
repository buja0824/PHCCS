package PHCCS.web.service;

import PHCCS.jwt.JwtProperties;
import PHCCS.jwt.JwtUtil;
import PHCCS.web.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public void storeRefreshToken(String refreshToken) {
        tokenRepository.saveRefreshToken(refreshToken);
    }

}
