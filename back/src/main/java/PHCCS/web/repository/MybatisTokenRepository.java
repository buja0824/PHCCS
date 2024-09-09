/**
package PHCCS.web.repository;

import PHCCS.domain.RefreshToken;
import PHCCS.web.repository.TokenRepository;
import PHCCS.web.repository.mapper.TokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MybatisTokenRepository implements TokenRepository {

    private final TokenMapper mapper;

    @Override
    public int saveRefreshToken(RefreshToken refreshToken) {
        int isSuccess = mapper.saveRefreshToken(refreshToken);

        return isSuccess;
    }

    @Override
    public RefreshToken getRefreshTokenById(String id) {
        return mapper.getRefreshTokenById(id);
    }
}
 */
