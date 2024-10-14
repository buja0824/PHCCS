package PHCCS.service.member.token;

import PHCCS.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Repository
@Slf4j
public class TokenRepository {

    private static Map<String, RefreshToken> refreshTokenStore = new HashMap<>();


    public void saveRefreshToken(String tokenId, String refreshToken) {
        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setRefreshToken(refreshToken); // 추후 actual 수정 필요함
        log.info("extractId: {}", tokenId);
        refreshTokenStore.put(tokenId, refreshTokenObj);
        log.info("tokenid: {}", tokenId);
    }


    public boolean removeRefreshToken(String tokenId, String token){
        return refreshTokenStore.remove(tokenId) != null; // 삭제 성공시 true 아니면 false 반환
    }

    public String getRefreshTokenByToken(String tokenId, String token){
        RefreshToken refreshTokenObj = refreshTokenStore.get(tokenId);
        log.info("refreshtokenObj: {}", refreshTokenObj);
        if(refreshTokenObj == null){
            return null;
        }

        return refreshTokenObj.getRefreshToken();
    }
}

