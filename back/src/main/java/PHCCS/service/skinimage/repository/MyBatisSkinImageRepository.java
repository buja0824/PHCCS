package PHCCS.service.skinimage.repository;

import PHCCS.service.skinimage.SkinImage;
import PHCCS.service.skinimage.repository.mapper.SkinImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisSkinImageRepository implements SkinImageRepository{
    private final SkinImageMapper mapper;

    @Override
    public void saveImgInfo(SkinImage imgInfo) {
        mapper.saveImgInfo(imgInfo);
    }

    @Override
    public void deleteImgInfo(Long memberId, String dir) {
        mapper.deleteImgInfo(memberId, dir);
    }
}
