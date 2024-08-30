package PHCCS.web.controller;

import PHCCS.domain.Comment;
import PHCCS.web.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

//    private final CommentService commentService;

    @PostMapping("/add/{category}/{id}")
    public ResponseEntity<?> postComment(
            @PathVariable("category") String category,
            @PathVariable("id") Long postId,
            @RequestBody Comment comment){

        log.info("postComment()");

        return null;
    }


}
