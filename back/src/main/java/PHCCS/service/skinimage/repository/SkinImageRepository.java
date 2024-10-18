package PHCCS.service.skinimage.repository;

import org.springframework.web.multipart.MultipartFile;

public interface SkinImageRepository {

    void savePath(Long memberId, String dir);
}
