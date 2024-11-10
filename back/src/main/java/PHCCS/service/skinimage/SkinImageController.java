package PHCCS.service.skinimage;

import PHCCS.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 24 10 12
 * 사용자가 동물의 피부질환 부위를 촬영한 사진을 전송하면 받아서 ai 모델에 넘겨준는 컨트롤러
 * 클라이언트 애서 이미지 전송 -> 현재 컨틀롤러에서 파일에 저장, 디비에 사진 저장 경로 저장
 *  -> ai 모델에 사진 저장 경로 또는 사진 바이트 코드 전송
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SkinImageController {

    private final SkinImageService imageService;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/camera", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<String>> imageReceiver(
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "imageFile")MultipartFile image
    ) throws IOException {

        Long memberId = jwtUtil.extractSubject(token);
        log.info("memberId = {}", memberId);

        Mono<String> stringMono = imageService.imageSaveAndSend(image, memberId);
        log.info("stringMono : {}", stringMono);
        return ResponseEntity.ok()
                .body(stringMono);
    }
}