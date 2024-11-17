package PHCCS.service.comment;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.common.exception.InternalServerEx;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.response.ApiResponse;
import PHCCS.common.sse.SSEService;
import PHCCS.service.comment.dto.CommentAddDTO;
import PHCCS.service.comment.dto.CommentDTO;
import PHCCS.service.comment.dto.LikedCommentDTO;
import PHCCS.service.comment.dto.MyCommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final JwtUtil jwtUtil;
    private final CommentService service;
    private final SSEService sseService;

    @PostMapping("/add/{category}/{postId}")
    public ResponseEntity<?> addComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @RequestBody CommentAddDTO comment){

        log.info("postComment()");
        log.info("댓글본문 = {}", comment.getComment());
        Long loginMember = jwtUtil.extractSubject(token);
        if(loginMember == null){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
            throw new BadRequestEx("로그인하지 않은 사용자는 접근할 수 없습니다.");
        }
        boolean isSave = service.save(loginMember, category, postId, comment);
        if(isSave){

            sseService.addCommentAlarm(category, postId, comment, loginMember);
//            return ResponseEntity.ok("댓글 저장이 완료되었습니다.");
            return ApiResponse.successCreate();
        }else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 저장에 오류가 발생하였습니다.");
            throw new InternalServerEx("댓글 저장에 오류가 발생했습니다.");
        }
    }

    @GetMapping("/show/{category}/{postId}")
    public ResponseEntity<?> findAllComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId){

        log.info("findAllComment()");
        Long loginMember = jwtUtil.extractSubject(token);
        if(loginMember == null){
            //            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
            throw new BadRequestEx("로그인하지 않은 사용자는 접근할 수 없습니다.");
        }
        List<Comment> allComment = service.findAllComment(category, postId);
        return ResponseEntity.ok(allComment);
    }

    // 댓글 달린 게시판 식별 해야함
    @PutMapping("/update/{category}/{postId}/{commentId}")
    public ResponseEntity<?> updateComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDTO dto){
        log.info("updateComment()");
        Long loginMember = jwtUtil.extractSubject(token);
        if(loginMember == null){
            //            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
            throw new BadRequestEx("로그인하지 않은 사용자는 접근할 수 없습니다.");
        }
        service.updateComment(category, postId, commentId, dto);

        return ApiResponse.successUpdate();
    }

    @DeleteMapping("delete/{category}/{postId}/{commentId}")
    public ResponseEntity<?> deleteComment(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId){

        log.info("deleteComment()");
        Long loginMember = jwtUtil.extractSubject(token);
        if(loginMember == null){
            //            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
            throw new BadRequestEx("로그인하지 않은 사용자는 접근할 수 없습니다.");
        }
        service.deleteComment(category, postId, commentId);
        return ApiResponse.successDelete();
    }

    //내가 작성한 댓글 목록
    @GetMapping("/my")
    public ResponseEntity<?> myComments(@RequestHeader("Authorization") String token){
        Long memberId = jwtUtil.extractSubject(token);
        log.info("작성한 댓글 확인하는 memberId = {}", memberId);
        List<MyCommentDTO> myComments = service.showMyComments(memberId);

        return ResponseEntity.ok(myComments);
    }

    //좋아요 누른 댓글 보기
    @GetMapping("/liked-comments")
    public ResponseEntity<?> likedComments(@RequestHeader("Authorization") String token){
        Long memberId = jwtUtil.extractSubject(token);
        log.info("좋아요 누른 댓글 확인하는 memberId = {}", memberId);
        List<LikedCommentDTO> likedComments = service.showLikedComments(memberId);

        return ResponseEntity.ok(likedComments);
    }

    @PostMapping("/like/{category}/{postId}/{commentId}")
    public ResponseEntity<?> commentLike(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @RequestHeader("Authorization") String token,
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId")Long commentId){
        log.info("incrementLike()");
        Long loginMember = jwtUtil.extractSubject(token);
        log.info("댓글 좋아요 누르는 memberId = {}, 게시글 = {}, 댓글 = {}", loginMember, postId, commentId);

        if(loginMember == null){
            //            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인하지 않은 사용자는 접근할 수 없습니다.");
            throw new BadRequestEx("로그인하지 않은 사용자는 접근할 수 없습니다.");
        }
        String string = service.incrementLike(loginMember, category, postId, commentId);
        return ApiResponse.successCreate(string);
    }
}
