package PHCCS.web.repository;

import PHCCS.domain.RefreshToken;
import PHCCS.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class TokenRepository {

    private static Map<String, RefreshToken> refreshTokenStore = new HashMap<>();
    // private static BigInteger sequence = BigInteger.ZERO;
    private RefreshToken refreshToken;
    private final JwtUtil jwtUtil;

    public void saveRefreshToken(String refreshToken) {
        // sequence = sequence.add(BigInteger.ONE);
        this.refreshToken = new RefreshToken();
        this.refreshToken.setRefreshToken(refreshToken);
        refreshTokenStore.put(jwtUtil.extractId(refreshToken), this.refreshToken);
    }


    public boolean removeRefreshToken(String token){
        return refreshTokenStore.remove(jwtUtil.extractId(token)) != null; // 삭제 성공시 true 아니면 false 반환
    }

    private String getRefreshTokenByToken(String token){
        return refreshTokenStore.get(jwtUtil.extractId(token)).getRefreshToken();
    }

}
