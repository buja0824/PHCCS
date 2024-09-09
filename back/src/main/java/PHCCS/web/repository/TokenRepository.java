package PHCCS.web.repository;

import PHCCS.domain.RefreshToken;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Repository
public class TokenRepository {

    private static Map<BigInteger, RefreshToken> refreshTokenStore = new HashMap<>();
    private static BigInteger sequence = BigInteger.ZERO;
    private RefreshToken refreshToken;

    public void saveRefreshToken(String refreshToken) {
        sequence = sequence.add(BigInteger.ONE);
        this.refreshToken.setRefreshToken(refreshToken);
        refreshTokenStore.put(sequence, this.refreshToken);
    }

    public RefreshToken getRefreshTokenById(String id) {
        return refreshTokenStore.get(new BigInteger(id));
    }

}
