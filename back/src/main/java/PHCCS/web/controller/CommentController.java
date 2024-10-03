package PHCCS.web.controller;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDTO;
import PHCCS.web.service.CommentService;
import PHCCS.web.service.SSEService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final SSEService sseService;

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
        boolean isSave = service.save(category, postId, comment);
        if(isSave){
            sseService.addCommentAlarm(category, postId, comment);
            return ResponseEntity.ok("댓글 저장이 완료되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 저장에 오류가 발생하였습니다.");
    }

    @GetMapping("/show/{category}/{id}")
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
            @RequestBody CommentDTO dto){
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
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId")Long commentId){
        log.info("incrementLike()");
//        if(!isLogin(loginMember)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
//        }
        service.incrementLike(/*loginMember.getId()*/2L, category, postId, commentId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
