package PHCCS.service.skinimage.repository;

import PHCCS.service.skinimage.SkinImage;

public interface SkinImageRepository {

    void saveImgInfo(SkinImage imgInfo);
    void deleteImgInfo(Long memberId, String dir);
}
