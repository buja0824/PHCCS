package PHCCS.web.controller;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDto;
import PHCCS.web.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping("/add/{category}/{id}")
    public ResponseEntity<?> addComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @PathVariable("category") String category,
            @PathVariable("id") Long postId,
            @RequestBody Comment comment){

        log.info("postComment()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        ResponseEntity<?> save = service.save(category, postId, comment);
        return save;
    }

    @GetMapping("/show-comments/{category}/{id}")
    public ResponseEntity<List<Comment>> findAllComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @PathVariable("category") String category,
            @PathVariable("id") Long postId){

        log.info("findAllComment()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        List<Comment> allComment = service.findAllComment(category, postId);
        return ResponseEntity.ok(allComment);
    }

    // 댓글 달린 게시판 식별 해야함
    @PutMapping("/update/{category}/{postId}/{commentId}")
    public ResponseEntity<?> updateComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto dto){
        log.info("updateComment()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        service.updateComment(category, postId, commentId, dto);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("delete/{category}/{postId}/{commentId}")
    public ResponseEntity<?> deleteComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId){

        log.info("deleteComment()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        service.deleteComment(category, postId, commentId);
        return null;
    }

    @PostMapping("/like/{category}/{postId}/{commentId}")
    public ResponseEntity<?> incrementLike(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestParam("increaseFlag") boolean flag,
            @PathVariable("category") String category,
            @PathVariable("postId")Long postId,
            @PathVariable("commentId")Long commentId){
        log.info("incrementLike()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        service.incrementLike(category, postId, commentId, flag);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
