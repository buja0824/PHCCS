package PHCCS.web.controller;

import PHCCS.SessionConst;
import PHCCS.domain.Member;
import PHCCS.domain.Post;
import PHCCS.domain.UploadFile;
import PHCCS.file.FileStore;
import PHCCS.web.repository.domain.PostModifyParam;
import PHCCS.web.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final PostService service;
    private final FileStore fileStore;
    @PostMapping(value = "/post", consumes = "multipart/form-data")
    public ResponseEntity<?> createPost(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestPart("dto") PostDto dto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> videoFiles)  throws IOException {

        log.info("createPost()");
        log.info("dto: {}", dto);
        log.info("imageFiles: {}", imageFiles);
        log.info("videoFiles: {}", videoFiles);
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }

        // 업로드한 이미지 저장
        if(imageFiles != null && !imageFiles.isEmpty()) {
            List<UploadFile> storeImgs = fileStore.storeFiles(imageFiles,
                    /*loginMember.getId()*/1L,
                    dto.getTitle(),
                    dto.getCategory());
            log.info("storeImgs: {}", storeImgs);
        }

        // 업로드한 동영상 저장
        if(videoFiles != null && !videoFiles.isEmpty()) {
            List<UploadFile> storeVids = fileStore.storeFiles(videoFiles,
                    /*loginMember.getId()*/2L,
                    dto.getTitle(),
                    dto.getCategory());
            log.info("storeVids: {}", storeVids);
        }
        Post post = new Post();
        post.setCategory(dto.getCategory());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(dto.getAuthor());
        post.setWriteTime(dto.getWriteTime());
//        post.setImageFiles(storeImgs);
//        post.setVideoFiles(storeVids);

        ResponseEntity<?> save = service.save(/*loginMember.getId()*/2L, post);
        return save;
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyPost(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestBody PostModifyParam modifyParam){

        log.info("modifyPost()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }

        service.modifyPost(loginMember.getId(), modifyParam);
        return  null;
    }


    public static boolean isLogin(Member loginMember){
        if(loginMember == null) return false;
        else return true;
    }

}
