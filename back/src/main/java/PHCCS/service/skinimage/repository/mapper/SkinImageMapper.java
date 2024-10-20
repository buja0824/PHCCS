package PHCCS.service.skinimage.repository.mapper;

import PHCCS.service.skinimage.SkinImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkinImageMapper {

    void saveImgInfo(SkinImage imgInfo);
}
