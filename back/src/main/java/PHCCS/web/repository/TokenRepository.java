package PHCCS.web.repository;

import PHCCS.domain.RefreshToken;
import PHCCS.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class TokenRepository {

    private static Map<String, RefreshToken> refreshTokenStore = new HashMap<>();
    private final JwtUtil jwtUtil;

    public void saveRefreshToken(String refreshToken) {
        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setRefreshToken(refreshToken);
        refreshTokenStore.put(jwtUtil.extractId(refreshToken), refreshTokenObj);
    }


    public boolean removeRefreshToken(String token){
        return refreshTokenStore.remove(jwtUtil.extractId(token)) != null; // 삭제 성공시 true 아니면 false 반환
    }

    public String getRefreshTokenByToken(String token){
        RefreshToken refreshTokenObj = refreshTokenStore.get(jwtUtil.extractId(token));
        if(refreshTokenObj == null){
            return null;
        }

        return refreshTokenObj.getRefreshToken();
    }
}
