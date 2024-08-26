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

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final PostService service;
    private final FileStore fileStore;
    @PostMapping("/post")
    public ResponseEntity<?> createPost(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestBody PostDto dto,
            @RequestPart) throws IOException {

        log.info("createPost()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }


        // 업로드한 이미지 저장
        List<UploadFile> storeImgs = fileStore.storeFiles(dto.getImageFiles(),
                loginMember.getId(),
                dto.getTitle(),
                dto.getCategory());

        // 업로드한 동영상 저장
        List<UploadFile> storeVids = fileStore.storeFiles(dto.getVideoFiles(),
                loginMember.getId(),
                dto.getTitle(),
                dto.getCategory());

        Post post = new Post();
        post.setCategory(dto.getCategory());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(dto.getAuthor());
        post.setWriteTime(dto.getWriteTime());
        post.setImageFiles(storeImgs);
        post.setVideoFiles(storeVids);

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
