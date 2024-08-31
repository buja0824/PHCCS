package PHCCS.web.controller;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDto;
import PHCCS.web.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<?> postComment(
            @PathVariable("category") String category,
            @PathVariable("id") Long postId,
            @RequestBody Comment comment){

        log.info("postComment()");
        ResponseEntity<?> save = service.save(category, postId, comment);
        return save;
    }

    @GetMapping("/show-comments/{category}/{id}")
    public ResponseEntity<List<Comment>> findAllComment(
            @PathVariable("category") String category,
            @PathVariable("id") Long postId){

        log.info("findAllComment()");
        List<Comment> allComment = service.findAllComment(category, postId);
        return ResponseEntity.ok(allComment);
    }

    // 댓글 달린 게시판 식별 해야함
    @PutMapping("/update/{category}/{postId}/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable("category") String category,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDto dto){
        log.info("updateComment()");
        service.updateComment(category, postId, commentId, dto);

        return ResponseEntity.ok(HttpStatus.OK);
    }



}
