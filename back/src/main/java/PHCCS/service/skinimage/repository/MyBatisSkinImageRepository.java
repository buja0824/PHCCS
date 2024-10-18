package PHCCS.service.skinimage.repository;

import PHCCS.service.skinimage.repository.mapper.SkinImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisSkinImageRepository implements SkinImageRepository{
    private final SkinImageMapper mapper;

    @Override
    public void savePath(Long memberId, String dir) {
        mapper.savePath(memberId, dir);
    }
}
