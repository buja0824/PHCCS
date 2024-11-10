package PHCCS.service.skinimage.repository.mapper;

import PHCCS.service.skinimage.SkinImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SkinImageMapper {
    void saveImgInfo(SkinImage imgInfo);
}
