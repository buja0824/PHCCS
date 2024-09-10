package PHCCS.web.controller;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDTO;
import PHCCS.web.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest
class CommentControllerTest {

    @Autowired private CommentService service;

    @Test
    void addCommentTest(){
        Comment comment = new Comment();
        comment.setComment("저도 댓글달아요");
        comment.setNickName("테스터33");
        comment.setMemberId(2L);
        comment.setWriteTime(LocalDateTime.now());
        comment.setLikeCnt(0L);
        service.save("qna_board", 3L, comment);
    }

    @Test
    void showAllCommentTest(){
        List<Comment> allComment = service.findAllComment("qna_board", 3L);
        log.info("allComment = {}", allComment.toString());
    }

    @Test
    void updateCommentTest(){
        CommentDTO commentDto = new CommentDTO();
        commentDto.setComment("수정합니다 댓글 수정");

        service.updateComment("qna_board", 3L, 7L, commentDto);
    }

    @Test
    void deleteCommentTest(){

    }
    @Test
    void increaseLike(){
        service.incrementLike(2L, "qna_board", 3L, 7L);
    }
}