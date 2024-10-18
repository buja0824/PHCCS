package PHCCS.service.skinimage;

import PHCCS.common.file.FileStore;
import PHCCS.common.file.UploadFile;
import PHCCS.service.skinimage.repository.SkinImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkinImageService {

    private final SkinImageRepository imageRepository;

    private final FileStore fileStore;

    public void imageSaveAndSend(MultipartFile image, Long memberId){

        try {
            // TODO 사용자가 보낸 사진 저장하기, 저장경로 반환
            // 사진저장
            UploadFile storeFile = fileStore.storeFile(image, memberId);
            // 저장경로 반환
            String dir = storeFile.getFileDir();
            imageRepository.savePath(memberId, dir);
            // TODO 사용자가 보낸 사진 저장경로 AI모델에게 전송, 검사결과 반환

            // TODO 검사결과를 사용자에게 반환, 검사결과 DB에 저장
        }catch (Exception e){

        }

    }
}
