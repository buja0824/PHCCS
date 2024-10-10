package PHCCS.web.repository.mapper;

import PHCCS.service.member.token.RefreshToken;
import org.apache.ibatis.annotations.Param;

public interface TokenMapper {

    // refresh 토큰 삽입
    int insertRefreshToken(RefreshToken refreshToken);

    // id로 refresh 토큰 검색
    RefreshToken getRefreshTokenById(@Param("id") String id);

}
