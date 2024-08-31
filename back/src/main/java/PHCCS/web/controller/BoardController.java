package PHCCS.web.controller;

import PHCCS.domain.Member;
import PHCCS.web.repository.domain.PostUpdateDto;
import PHCCS.web.service.PostService;
import PHCCS.web.service.domain.FileDto;

import PHCCS.web.service.domain.PostDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final PostService service;
    @PostMapping(value = "/post", consumes = "multipart/form-data")
    public ResponseEntity<?> createPost(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestPart("dto") PostDto dto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> videoFiles)  throws IOException {
        log.info("|co|createPost()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }

        ResponseEntity<?> save = service.save(/*loginMember.getId()*/2L, dto, imageFiles, videoFiles);
        return save;
    }

    @GetMapping("/show/{category}/{id}")
    public ResponseEntity<?> showPost(
            @PathVariable("category") String category,
            @PathVariable("id") Long id){

        log.info("showPost()");

        ResponseEntity<?> post = service.showPost(category, id);

        return post;
    }

    @GetMapping("/images/{uuid}")
    public ResponseEntity<Resource> sendImgFile(
            @PathVariable("uuid") String filename,
            @RequestBody FileDto dto) throws MalformedURLException {
        Resource resource = service.sendFile(filename, dto);
        MediaType mediaType = determineImgMediaType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
    @GetMapping("/video/{uuid}")
    public ResponseEntity<Resource> sendVidFile(
            @PathVariable("uuid") String filename,
            @RequestBody FileDto dto) throws MalformedURLException {
        Resource resource = service.sendFile(filename, dto);
        MediaType mediaType = determineVideoMediaType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/show/{category}")
    public ResponseEntity<?> showAllPost(@PathVariable("category") String category){
        log.info("showAllPost()");
        log.info("category = {}", category);
        ResponseEntity<?> posts = service.showAllPost(category);
        return posts;
    }

    @PutMapping(value = "/update/{category}/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updatePost(
/*            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,*/
            @PathVariable("category") String category,
            @PathVariable("id") Long postId,
            @RequestPart("updateParam") PostUpdateDto updateParam,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imgFiles,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> vidFiles) throws IOException {

        log.info("updatePost()");
        log.info("imgFiles = {}", imgFiles);
        log.info("vidFiles = {}", vidFiles);
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        ResponseEntity<?> responseEntity = service.updatePost(/*loginMember.getId()*/2L, category, postId, updateParam, imgFiles, vidFiles);
        return responseEntity;
    }

    @DeleteMapping("/delete/{category}/{id}")
    public ResponseEntity<?> deletePost(
/*            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,*/
            @PathVariable("category") String category,
            @PathVariable("id") Long postId){
        log.info("deletePost()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        service.deletePost(category, /*loginMember.getId()*/2L, postId);
        return ResponseEntity.ok().body("삭제 완료");
    }


    private static boolean isLogin(Member loginMember){
        if(loginMember == null) return false;
        else return true;
    }

    private MediaType determineImgMediaType(String filename) {
        if (filename.endsWith(".jpg")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        return MediaType.ALL;
    }

    private MediaType determineVideoMediaType(String filename) {
        if (filename.endsWith(".mp4")) {
            return MediaType.parseMediaType("video/mp4");
        } else if (filename.endsWith(".avi")) {
            return MediaType.parseMediaType("video/x-msvideo");
        } else if (filename.endsWith(".mov")) {
            return MediaType.parseMediaType("video/quicktime");
        } else if (filename.endsWith(".mkv")) {
            return MediaType.parseMediaType("video/x-matroska");
        }
        return MediaType.APPLICATION_OCTET_STREAM; // 기본 값인데 이걸 보내는게 맞나?
    }

}
