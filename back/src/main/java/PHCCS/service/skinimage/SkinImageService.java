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

    public static final String CAT = "고양이";
    public static final String DOG = "강아지";
    public static final String SYMPTOM = "유증상";
    public static final String NON_SYMPTOM = "무증상";
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
        final String finalBreed;
        if(chart.getBreed()) finalBreed = CAT; else finalBreed = DOG;

        final String finalSymptom;
        if(chart.getSymptom()) finalSymptom = SYMPTOM; else finalSymptom = NON_SYMPTOM;
        return testResult.flatMap(result ->{
            String imgResult;
            try {
                ImgResultDTO imgResultDTO = objectMapper.readValue(result, ImgResultDTO.class);
                imgResult = imgResultDTO.getImgResult();  //구현화면 보여줘에뮬레이터가 없어서 영상 찍은거 보여들ㄹ께여ㅛ
            } catch (JsonProcessingException e) {
                throw new InternalServerEx("검사결과를 처리하지 못하였습니다.");
            }
            SkinImage imgInfo = new SkinImage();
            imgInfo.setMemberId(memberId);
            imgInfo.setBreed(finalBreed);
            imgInfo.setSymptom(finalSymptom);
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









