package PHCCS.service.skinimage;

import PHCCS.common.config.WebConfig;
import PHCCS.common.exception.InternalServerEx;
import PHCCS.common.file.FileStore;
import PHCCS.common.file.UploadFile;
import PHCCS.service.skinimage.dto.Chart;
import PHCCS.service.skinimage.dto.ImgResultDTO;
import PHCCS.service.skinimage.repository.SkinImageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkinImageService {

    private final SkinImageRepository repository;
    private final FileStore fileStore;
    private final WebConfig webConfig;
    private final ObjectMapper objectMapper;

    public Mono<String> imageSaveAndSend(MultipartFile image, Long memberId, Chart chart) throws JsonProcessingException {

        UploadFile storeFile;
        String dir = "";

        // 캐리지리턴, 라인피드 제거
        log.info("chart = {}", chart);

        try {
            storeFile = fileStore.storeFile(image, memberId);
        } catch (IOException e) {
            throw new InternalServerEx(e.getMessage());
        }
        // 저장경로 반환
        dir = storeFile.getFileDir();
        log.info("dir : {}", dir);
        String json =
                "{" +
                    "\"dir\": \"" + dir + "\" ," +
                    " \"breed\": \"" + chart.getBreed() + "\", " +
                    "\"symptom\": \"" + chart.getSymptom() + "\"" +
                "}";
        log.info("json = {}", json);

        // 파이썬 서버에 전송
        Mono<String> testResult = webConfig.aiImageServer()
                .post()
                .body(BodyInserters.fromValue(json))
                .retrieve()
                .bodyToMono(String.class);
        log.info("testResult = {}", testResult);

        final String finalDir = dir;
        return testResult.flatMap(result ->{
            String imgResult;
            try {
                ImgResultDTO imgResultDTO = objectMapper.readValue(result, ImgResultDTO.class);
                imgResult = imgResultDTO.getImgResult();
            } catch (JsonProcessingException e) {
                throw new InternalServerEx("검사결과를 처리하지 못하였습니다.");
            }
            SkinImage imgInfo = new SkinImage();
            imgInfo.setMemberId(memberId);
            imgInfo.setDir(finalDir);
            imgInfo.setResult(imgResult);
            imgInfo.setCreateAt(LocalDateTime.now());
            repository.saveImgInfo(imgInfo);

            return Mono.just(result);
        });
    }
    // 사용 안함
//    @Transactional
//    public void deleteImgInfo(Long memberId, String fileName){
//        String fullPath = fileStore.getFullPath(fileName, memberId);
//        log.info("삭제할 사진이 있는 fullPath = {}", fullPath);
//        repository.deleteImgInfo(memberId, fullPath);
//        try{
//            fileStore.deleteFiles(fullPath);
//        }catch(Exception e){
//            throw new InternalServerEx("데이터베이스 사진 삭제중 오류 발생");
//        }
//    }
}









