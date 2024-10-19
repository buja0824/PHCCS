package PHCCS.service.skinimage;

import PHCCS.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping("/camera")
    public ResponseEntity<?> imageReceiver(
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "imageFile")MultipartFile image
    ) throws IOException {

        Long memberId = jwtUtil.extractSubject(token);
        log.info("memberId = {}", memberId);

        String testResult = imageService.imageSaveAndSend(image, memberId);

        return ResponseEntity.ok().body(testResult);
    }

}