package PHCCS.service.skinimage.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SkinImageMapper {

    void savePath(@Param("memberId") Long memberId, @Param("dir") String dir);
}
