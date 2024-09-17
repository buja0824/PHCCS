package PHCCS.web.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMapper {
    void save(@Param("roomId") Long roomId, @Param("memberId") Long memberId, @Param("chat") String chat);
}
