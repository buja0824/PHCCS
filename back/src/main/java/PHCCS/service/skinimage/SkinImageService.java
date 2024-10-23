package PHCCS.service.skinimage;

import PHCCS.common.config.WebConfig;
import PHCCS.common.file.FileStore;
import PHCCS.common.file.UploadFile;
import PHCCS.service.skinimage.dto.ImgResultDTO;
import PHCCS.service.skinimage.repository.SkinImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkinImageService {

    private final SkinImageRepository imageRepository;
    private final FileStore fileStore;
    private final WebConfig webConfig;

    public Mono<String> imageSaveAndSend(MultipartFile image, Long memberId) throws IOException {

        UploadFile storeFile;
        SkinImage imgInfo = new SkinImage();
        String dir = "";
        try {
            storeFile = fileStore.storeFile(image, memberId);
            // 저장경로 반환
            dir = storeFile.getFileDir();
            log.info("dir : {}", dir);
            String json = "{\"dir\": \"" + dir + "\"}";
            imgInfo.setMemberId(memberId);
            imgInfo.setDir(dir);
            imgInfo.setCreateAt(LocalDate.now());
            //            imageRepository.savePath(memberId, dir);
            // 파이썬 서버에 전송
            return webConfig.aiImageServer()
                    .post()
                    .body(BodyInserters.fromValue(json))
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (Exception e) {
            if(!dir.isEmpty()) fileStore.deleteFiles(dir);
            throw e;
        }
    }
}
