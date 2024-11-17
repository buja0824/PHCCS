package PHCCS.service.post;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.common.response.ApiResponse;
import PHCCS.service.member.Member;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.file.FileDTO;

import PHCCS.service.post.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final JwtUtil jwtUtil;
    private final PostService service;
    private final ObjectMapper mapper = new ObjectMapper();
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPost(
            @RequestHeader("Authorization") String token,
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> videoFiles) throws IOException {

        log.info("|co|createPost()");
        Long memberId = jwtUtil.extractSubject(token);
        PostDTO dto = mapper.readValue(dtoJson, PostDTO.class);

        service.save(memberId, dto, imageFiles, videoFiles);
        return ApiResponse.successCreate();
    }

    @GetMapping("/show/{category}/{id}")
    public ResponseEntity<?> showPost(
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("id") Long id){
        log.info("showPost()");
        Long memberId = jwtUtil.extractSubject(token);

        Post post = service.showPost(category, id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/file/{uuid}/{category}/{postId}")
    public ResponseEntity<Resource> getFile(
            @PathVariable("uuid") String filename,
            @PathVariable("category") String category,
//            @PathVariable("memberId") Long memberId,
            @PathVariable("postId") Long postId
//            @PathVariable("title") String title
            /*@RequestBody FileDTO dto*/) throws IOException {

        FileDTO dto = new FileDTO();
        dto.setCategory(category);
        dto.setPostId(postId);
//        dto.setTitle(title);
        Path path = service.getPath(filename, dto);
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

    @GetMapping("/show/{category}")
    public ResponseEntity<?> showAllPost(
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @RequestParam(name = "searchName", defaultValue = "") String searchName,
            @RequestParam(name = "page", defaultValue = "1") Long page,
            @RequestParam(name = "size", defaultValue = "15") Long size){
        log.info("showAllPost() Category: {}, Page: {}, Size: {}", category, page, size);
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        if(category == null || category.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 접근 입니다.");
            throw new BadRequestEx("잘못된 접근 입니다.");
        }
        log.info("searchName = {}", searchName);

        List<PostHeaderDTO> posts = service.showAllPost(category,searchName, page, size);
        if(posts == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글을 불러오지 못하였습니다.");
        return ResponseEntity.ok(posts);
    }

    @PutMapping(value = "/update/{category}/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePost(
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("id") Long postId,
            @RequestPart("updateParam") String updateDTO,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imgFiles,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> vidFiles) throws IOException {

        log.info("updatePost()");
        log.info("imgFiles = {}", imgFiles);
        log.info("vidFiles = {}", vidFiles);
        Long memberId = jwtUtil.extractSubject(token);
        PostUpdateDTO updateParam = mapper.readValue(updateDTO, PostUpdateDTO.class);
//        ResponseEntity<?> responseEntity = service.updatePost(memberId, category, postId, updateParam, imgFiles, vidFiles);
        service.updatePost(memberId, category, postId, updateParam, imgFiles, vidFiles);
        return ApiResponse.successUpdate();
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
        return ApiResponse.successDelete();
    }

    @GetMapping("/my")
    public ResponseEntity<?> showMyPost(@RequestHeader("Authorization") String token){
        Long memberId = jwtUtil.extractSubject(token);
        List<MyPostDTO> posts = service.showMyPost(memberId);
        return ResponseEntity.ok(posts);
    }

    //내가 좋아요 누른글 목록
    @GetMapping("/liked-posts")
    public ResponseEntity<?> likedPosts(@RequestHeader("Authorization") String token){
        Long memberId = jwtUtil.extractSubject(token);
        List<LikedPostDTO> likedPosts = service.showLikedPosts(memberId);

        return ResponseEntity.ok(likedPosts);
    }

    @PostMapping("/like/{category}/{id}")
    public ResponseEntity<?> likePost(
        @RequestHeader("Authorization") String token,
        @PathVariable("category") String category,
        @PathVariable("id") Long id){

        Long memberId = jwtUtil.extractSubject(token);
        log.info("게시글 좋아요 누르는 memberId = {}, 게시글 = {}", memberId, id);

        String string = service.likePost(memberId, category, id);
        return ApiResponse.successCreate(string);
    }

    private static boolean isLogin(Member loginMember){
        if(loginMember == null) return false;
        else return true;
    }
//
//    private MediaType determineImgMediaType(String filename) {
//        if (filename.endsWith(".jpg")) {
//            return MediaType.IMAGE_JPEG;
//        } else if (filename.endsWith(".jpeg")) {
//            return MediaType.IMAGE_JPEG;
//        } else if (filename.endsWith(".png")) {
//            return MediaType.IMAGE_PNG;
//        } else if (filename.endsWith(".gif")) {
//            return MediaType.IMAGE_GIF;
//        }
//        return MediaType.ALL;
//    }
//
//    private MediaType determineVideoMediaType(String filename) {
//        if (filename.endsWith(".mp4")) {
//            return MediaType.parseMediaType("video/mp4");
//        } else if (filename.endsWith(".avi")) {
//            return MediaType.parseMediaType("video/x-msvideo");
//        } else if (filename.endsWith(".mov")) {
//            return MediaType.parseMediaType("video/quicktime");
//        } else if (filename.endsWith(".mkv")) {
//            return MediaType.parseMediaType("video/x-matroska");
//        }
//        return MediaType.APPLICATION_OCTET_STREAM; // 기본 값인데 이걸 보내는게 맞나?
//    }

}
