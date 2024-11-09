package PHCCS.service.skinimage;

import PHCCS.common.config.WebConfig;
import PHCCS.common.exception.InternalServerEx;
import PHCCS.common.file.FileStore;
import PHCCS.common.file.UploadFile;
import PHCCS.service.skinimage.dto.ImgResultDTO;
import PHCCS.service.skinimage.repository.SkinImageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkinImageService {

    private final SkinImageRepository imageRepository;
    private final FileStore fileStore;
    private final WebConfig webConfig;
    private final ObjectMapper objectMapper;

    public Mono<String> imageSaveAndSend(MultipartFile image, Long memberId)  {

        UploadFile storeFile;
//        SkinImage imgInfo = new SkinImage();
        String dir = "";
        try {
            storeFile = fileStore.storeFile(image, memberId);
        } catch (IOException e) {
            throw new InternalServerEx(e.getMessage());
        }
        // 저장경로 반환
        dir = storeFile.getFileDir();
        log.info("dir : {}", dir);
        String json = "{\"dir\": \"" + dir + "\"}";
//            imgInfo.setMemberId(memberId);
//            imgInfo.setDir(dir);
        //            imageRepository.savePath(memberId, dir);
        // 파이썬 서버에 전송
        Mono<String> testResult = webConfig.aiImageServer()
                .post()
                .body(BodyInserters.fromValue(json))
                .retrieve()
                .bodyToMono(String.class);
        log.info("testResult = {}", testResult);
        String finalDir = dir; // 람다식 내부에서 사용되는 변수는 변경이 불가능한 상태로 들어와ㅑㅇ 하기때문에
        testResult.subscribe(result ->{
            String s;
            try {
                ImgResultDTO imgResultDTO = objectMapper.readValue(result, ImgResultDTO.class);
//                    s = convertUnicode(imgResultDTO.getImgResult());
                s = imgResultDTO.getImgResult();
            } catch (JsonProcessingException e) {
                throw new InternalServerEx("검사결과를 처리하지 못하였습니다.");
            }
            SkinImage imgInfo = new SkinImage();
            imgInfo.setMemberId(memberId);
            imgInfo.setDir(finalDir);
            imgInfo.setResult(s);
            imgInfo.setCreateAt(LocalDateTime.now());
            imageRepository.saveImgInfo(imgInfo);
        });
        log.info("다 해야 결과 나오겠지?");
        return testResult;
    }

}
