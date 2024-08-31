package PHCCS.web.controller;

import PHCCS.domain.Comment;
import PHCCS.web.repository.domain.CommentDto;
import PHCCS.web.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CommentControllerTest {

    @Autowired private CommentService service;

    @Test
    void addCommentTest(){
        Comment comment = new Comment();
        comment.setComment("세번째");
        comment.setAuthor("테스터");
        comment.setMemberId(3L);
        comment.setWriteTime(LocalDateTime.now());
        service.save("qna_board", 1L, comment);
    }

    @Test
    void showAllCommentTest(){
        List<Comment> allComment = service.findAllComment("qna_board", 1L);
        log.info("allComment = {}", allComment.toString());
    }

    @Test
    void updateCommentTest(){
        CommentDto commentDto = new CommentDto();
        commentDto.setComment("수정합니다");

        service.updateComment("qna_board", 1L, 2L, commentDto);
    }
}