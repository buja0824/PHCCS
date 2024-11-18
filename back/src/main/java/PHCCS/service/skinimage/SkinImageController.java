package PHCCS.service.skinimage;

import PHCCS.common.file.FileStore;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.service.skinimage.dto.Chart;
import PHCCS.service.skinimage.repository.SkinImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 24 10 12
 * 사용자가 동물의 피부질환 부위를 촬영한 사진을 전송하면 받아서 ai 모델에 넘겨준는 컨트롤러
 * 클라이언트 애서 이미지 전송 -> 현재 컨틀롤러에서 파일에 저장, 디비에 사진 저장 경로 저장
 *  -> ai 모델에 사진 저장 경로 또는 사진 바이트 코드 전송
 *
 */
@Slf4j
@RestController
@RequestMapping("/camera")
@RequiredArgsConstructor
public class SkinImageController {

    private final SkinImageService service;
    private final SkinImageRepository repository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final FileStore fileStore;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<String>> imageReceiver(
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "imageFile")MultipartFile image,
            @RequestPart("chart") String chart
    ) throws IOException {

        Long memberId = jwtUtil.extractSubject(token);
        log.info("memberId = {}", memberId);

        log.info("종류 및 차트 문자열 자체 = {}", chart);
        log.info("====");
        Chart chartObj = objectMapper.readValue(chart, Chart.class);
        log.info("Chart : {}", chartObj);
        Mono<String> stringMono = service.imageSaveAndSend(image, memberId, chartObj);
        log.info("stringMono : {}", stringMono);
        return ResponseEntity.ok()
                .body(stringMono);
    }

    @DeleteMapping("/{uuid}")
    public void deleteTestLog(
            @RequestHeader("Authorization") String token,
            @RequestParam("uuid") String fileName){

        Long memberId = jwtUtil.extractSubject(token);
        service.deleteImgInfo(memberId, fileName);

    }


    @GetMapping("/file/{uuid}")
    public ResponseEntity<Resource> getFile(
            @RequestHeader("Authorization") String token,
            @PathVariable("uuid") String filename) throws IOException {

        Long memberId = jwtUtil.extractSubject(token);
        Path path = getPath(filename, memberId);
        log.info("path: {} ", path);
//        MediaType mediaType = determineImgMediaType(filename);
        MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(path));
        log.info("mediaType: {}", mediaType);
        log.info("path.toUri(): {} ", path.toUri());
        Resource resource = new UrlResource(path.toUri());
        log.info("resource: {}", resource);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
    private Path getPath(String filename, Long memberId){
        String fullPath = fileStore.getFullPath(filename, memberId);
        Path filePath = Paths.get(fullPath).normalize();
        return filePath;
    }
}