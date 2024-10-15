package PHCCS.service.post;

import PHCCS.service.member.Member;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.file.FileDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
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

    private final JwtUtil jwtUtil;
    private final PostService service;
    @PostMapping(value = "/post", consumes = "multipart/form-data")
    public ResponseEntity<?> createPost(
            @RequestHeader("Authorization") String token,
            @RequestPart("dto") PostDTO dto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> videoFiles)  throws IOException {

        log.info("|co|createPost()");
        Long memberId = jwtUtil.extractSubject(token);

        ResponseEntity<?> save = service.save(memberId, dto, imageFiles, videoFiles);
        return save;
    }

    @GetMapping("/show/{category}/{id}")
    public ResponseEntity<?> showPost(
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("id") Long id){
        log.info("showPost()");
        Long memberId = jwtUtil.extractSubject(token);

        ResponseEntity<?> post = service.showPost(category, id);
        return post;
    }

    @GetMapping("/image/{uuid}")
    public ResponseEntity<Resource> sendImgFile(
            @PathVariable("uuid") String filename,
            @RequestBody FileDTO dto) throws MalformedURLException {
        Resource resource = service.sendFile(filename, dto);
        MediaType mediaType = determineImgMediaType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
    @GetMapping("/video/{uuid}")
    public ResponseEntity<Resource> sendVidFile(
            @PathVariable("uuid") String filename,
            @RequestBody FileDTO dto) throws MalformedURLException {

        Resource resource = service.sendFile(filename, dto);
        MediaType mediaType = determineVideoMediaType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/show/{category}")
    public ResponseEntity<?> showAllPost(
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @RequestParam(name = "page", defaultValue = "1") Long page,
            @RequestParam(name = "size", defaultValue = "15") Long size){
        log.info("showAllPost() Category: {}, Page: {}, Size: {}", category, page, size);
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }

        if(category == null || category.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 접근 입니다.");

        List<Post> posts = service.showAllPost(category, page, size);
        if(posts == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글을 불러오지 못하였습니다.");
        return ResponseEntity.ok(posts);
    }

    @PutMapping(value = "/update/{category}/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updatePost(
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("id") Long postId,
            @RequestPart("updateParam") PostUpdateDTO updateParam,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imgFiles,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> vidFiles) throws IOException {

        log.info("updatePost()");
        log.info("imgFiles = {}", imgFiles);
        log.info("vidFiles = {}", vidFiles);
        Long memberId = jwtUtil.extractSubject(token);

//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        ResponseEntity<?> responseEntity = service.updatePost(memberId, category, postId, updateParam, imgFiles, vidFiles);
        return responseEntity;
    }

    @DeleteMapping("/delete/{category}/{id}")
    public ResponseEntity<?> deletePost(
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("id") Long postId){
        log.info("deletePost()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        Long memberId = jwtUtil.extractSubject(token);

        service.deletePost(category, memberId, postId);
        return ResponseEntity.ok().body("삭제 완료");
    }

    @GetMapping("/my")
    public ResponseEntity<?> showMyPost(@RequestHeader("Authorization") String token){
        Long memberId = jwtUtil.extractSubject(token);
        List<MyPostDTO> posts = service.showMyPost(memberId);
        return ResponseEntity.ok(posts);
    }


    @PostMapping("/like/{category}/{id}")
    public ResponseEntity<?> likePost(
        @RequestHeader("Authorization") String token,
        @PathVariable("category") String category,
        @PathVariable("id") Long id){

        Long memberId = jwtUtil.extractSubject(token);
        service.likePost(memberId, category, id);

        return null;
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
